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
import game.bible.passage.context.PreContext
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.util.Date

private val log = KotlinLogging.logger {}

/**
 * Passage Generation Service Logic
 *
 * @since 13th January 2025
 */
@Service
class GenerationService(
    private val api: BibleApiConfig,
    private val bible: BibleConfig,
    private val chat: ChatGptConfig,
    private val client: OpenAIClient,
    private val mapper: ObjectMapper,
    private val restClient: RestClient
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

    /** Generates the context leading up to a given passage */
    fun preContext(passageKey: String): PreContext {
        log.info { "Asking ChatGPT for pre-context [$passageKey]" }

        val devPrompt: String = chat.getContext()!!.getPromptDeveloper()!!
        val userPrompt: String = chat.getContext()!!.getPromptUser()!! + passageKey

        var context = ""
        client.chat().completions().create(createParams(devPrompt, userPrompt)).choices().stream()
            .flatMap { choice: ChatCompletion.Choice -> choice.message().content().stream() }
            .forEach { x: String? -> context += x }

        return PreContext(passageKey, context)
    }

    private fun fetchText(passageId: String): String {
        log.info { "Attempting to fetch text for $passageId" }
        val url = "${api.getBaseUrl()}/$passageId"

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
         client.chat().completions().create(createParams(devPrompt, userPrompt)).choices().stream()
             .flatMap { choice: ChatCompletion.Choice -> choice.message().content().stream() }
             .forEach { x: String? -> summary += x }

         return summary
     }

    private fun createParams(devPrompt: String, userPrompt: String): ChatCompletionCreateParams{
        return ChatCompletionCreateParams.builder()
            .model(ChatModel.GPT_4O_MINI)
            .maxCompletionTokens(2048)
            .addDeveloperMessage(devPrompt)
            .addUserMessage(userPrompt)
            .build()
    }

}