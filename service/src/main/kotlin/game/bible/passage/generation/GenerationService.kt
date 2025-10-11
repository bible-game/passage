package game.bible.passage.generation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.openai.client.OpenAIClient
import com.openai.models.ChatCompletion
import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatModel
import game.bible.config.model.domain.BibleConfig
import game.bible.config.model.integration.BibleApiConfig
import game.bible.config.model.integration.ChatGptConfig
import game.bible.passage.Passage
import game.bible.passage.context.PostContext
import game.bible.passage.context.PreContext
import game.bible.passage.feedback.PromptType
import game.bible.passage.study.Question
import game.bible.passage.study.Study
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.document.MetadataMode
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.ai.openai.OpenAiAudioSpeechModel
import org.springframework.ai.openai.OpenAiAudioSpeechOptions
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.ai.openai.OpenAiEmbeddingOptions
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest.Voice.ALLOY
import org.springframework.ai.openai.api.OpenAiAudioApi.TtsModel.TTS_1_HD
import org.springframework.ai.openai.audio.speech.SpeechPrompt
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.util.Date
import java.util.stream.Stream


private val log = KotlinLogging.logger {}

/**
 * Generation Service Logic
 * @since 13th January 2025
 */
@Service
class GenerationService(
    private val api: BibleApiConfig,
    private val bible: BibleConfig,
    private val chat: ChatGptConfig,
    private val client: OpenAIClient,
    private val mapper: ObjectMapper,
    private val restClient: RestClient,
    private val speech: OpenAiAudioSpeechModel,
    private val embedding: OpenAiEmbeddingModel,
    private val redis: StringRedisTemplate
) {

    /** Generates a random bible passage */
    fun random(date: Date): Passage {
        val testament = bible.getTestaments()!!.random()

        val division = testament.getDivisions()!!.random()
        val book = division.getBooks()!!.random()
        val chapter = "${(1..book.getChapters()!!).random()}"
        val verses = book.getVerses()!![chapter.toInt() - 1]

        val text = fetchText("${book.getKey()!!}+$chapter")
        val summary = summarise(text)

        val icon = book.getIcons()!![chapter.toInt() - 1]

        return Passage(date, book.getName()!!, chapter, "", summary, verses, icon, text)
    }

    fun findKeysWithPrefix(prefix: String, count: Long = 100): Set<String> {
        val keys = mutableSetOf<String>()
        log.info { "Searching Redis for keys with prefix [$prefix]" }
        val scanOptions = ScanOptions.scanOptions().match("$prefix*").count(count).build()

        val cursor = redis.scan(scanOptions)
        while (cursor.hasNext()) {
            keys.add(cursor.next())
        }
        cursor.close()
        return keys
    }

    /** Generates the context leading up to a given passage */
    fun preContext(passageKey: String): PreContext {
        val cacheKey = "precontext:"

        val existingKeys = findKeysWithPrefix(cacheKey)

        var cachedPrompt: String? = null
        val latestKey = existingKeys.maxOrNull()

        if (latestKey != null) {
            cachedPrompt = redis.opsForValue().get(latestKey) as? String
            log.info { "Found pre-context prompt in Redis cache [$latestKey]" }
        } else {
            log.info { "No pre-context prompt found in Redis cache" }
        }

        log.info { "Asking ChatGPT for pre-context [$passageKey]" }

        val devPrompt: String = chat.getPreContext()!!.getPromptDeveloper()!!
        val userPrompt: String = cachedPrompt ?: (chat.getPreContext()!!.getPromptUser()!! + passageKey)

        var context = ""
        message(devPrompt, userPrompt).forEach { x: String? -> context += x }

        return PreContext(passageKey, context)
    }

    /** Generates the context after a given passage */
    fun postContext(passageKey: String): PostContext {
        log.info { "Asking ChatGPT for post-context [$passageKey]" }

        val devPrompt: String = chat.getPostContext()!!.getPromptDeveloper()!!
        val userPrompt: String = chat.getPostContext()!!.getPromptUser()!! + passageKey

        var context = ""
        message(devPrompt, userPrompt).forEach { x: String? -> context += x }

        return PostContext(passageKey, context)
    }

    /** Generates a study for a given passage */
    fun study(passageKey: String): Study {
        log.info { "Asking ChatGPT for study [$passageKey]" }

        val devPrompt: String = chat.getStudy()!!.getPromptDeveloper()!!
        val userPrompt: String = chat.getStudy()!!.getPromptUser()!! + passageKey

        var response = ""
        message(devPrompt, userPrompt).forEach { x: String? -> response += x }

        val study = Study(passageKey)
        val questions: List<Question> = mapper.readValue(
            response, mapper.typeFactory.constructCollectionType(List::class.java, Question::class.java))

        questions.forEach { it.study = study }
        study.questions = questions
        study.goldenSummary = goldenSummary(passageKey)

        return study
    }

    /** Generates a golden summary for a given passage */
    fun goldenSummary(passageKey: String): String {
        log.info { "Asking ChatGPT for golden summary [$passageKey]" }

        val devPrompt: String = chat.getGolden()!!.getPromptDeveloper()!!
        val userPrompt: String = chat.getGolden()!!.getPromptUser()!! + passageKey

        var summary = ""
        message(devPrompt, userPrompt).forEach { x: String? -> summary += x }

        return summary
    }

    /** Generates audio for a given passage */
    fun audio(passageKey: String): ByteArray {
        val text = fetchText(passageKey)

        log.info { "Asking OpenAI TTS for audio [$passageKey]" }
        val speechOptions = OpenAiAudioSpeechOptions.builder()
            .model(TTS_1_HD.value)
            .voice(ALLOY)
            .responseFormat(MP3)
            .speed(1.0f)
            .build()

        val prompt = SpeechPrompt(text, speechOptions)
        val response = speech.call(prompt)

        return response.result.output
    }


    /** Generates a new prompt based on user feedback */
    fun feedbackPrompt(feedback: String, promptType: PromptType): String {
        log.info { "Asking ChatGPT for new prompt based on feedback summary [$promptType]" }

        val existingPrompt = when (promptType) {
            PromptType.PRE_CONTEXT -> chat.getPreContext()!!.getPromptUser()!!
            PromptType.POST_CONTEXT -> chat.getPostContext()!!.getPromptUser()!!
            PromptType.DAILY -> chat.getDaily()!!.getPromptUser()!!
            PromptType.STUDY -> chat.getStudy()!!.getPromptUser()!!
            PromptType.GOLDEN -> chat.getGolden()!!.getPromptUser()!!
            PromptType.FEEDBACK -> chat.getFeedback()!!.getPromptUser()!!
        }

        val devPrompt: String = chat.getFeedback()!!.getPromptDeveloper()!! + feedback
        val userPrompt: String = chat.getGolden()!!.getPromptUser()!! + existingPrompt

        var prompt = ""
        message(devPrompt, userPrompt).forEach { x: String? -> prompt += x }

        return prompt
    }



    private fun fetchText(passageId: String): String {
        log.info { "Attempting to fetch text for $passageId" }
        val url = "${api.getBaseUrl()}/$passageId?single_chapter_book_matching=indifferent"

        val response = restClient.get()
                        .uri(url).retrieve()
                        .body(String::class.java)

        val jsonNode: JsonNode = mapper.readTree(response)
        return jsonNode["text"].asText()
    }

     private fun summarise(text: String): String {
         log.info { "Asking ChatGPT for text summary [${text.substring(0, 20)}...]" }

         val devPrompt: String = chat.getDaily()!!.getPromptDeveloper()!!
         val userPrompt: String = chat.getDaily()!!.getPromptUser()!! + text

         var summary = ""
         message(devPrompt, userPrompt).forEach { x: String? -> summary += x }

         return summary
     }

    /** Generate a vector embedding for a passage summary */
    fun embedding(summary: String): FloatArray {
        return embedding.embedForResponse(listOf(summary)).result.output
    }

    // question :: move to chat gpt utilities?
    private fun createParams(devPrompt: String, userPrompt: String): ChatCompletionCreateParams{
        return ChatCompletionCreateParams.builder()
            .model(ChatModel.GPT_4O_MINI)
            .maxCompletionTokens(2048)
            .addDeveloperMessage(devPrompt)
            .addUserMessage(userPrompt)
            .build()
    }

    // question :: move to chat gpt utilities?
    private fun message(devPrompt: String, userPrompt: String): Stream<String> {
        return client.chat().completions()
            .create(createParams(devPrompt, userPrompt)).choices().stream()
            .flatMap { choice: ChatCompletion.Choice -> choice.message().content().stream() }
    }

}
