package game.bible.passage.generation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import game.bible.common.util.log.Log
import game.bible.config.model.domain.BibleConfig
import game.bible.config.model.integration.BibleApiConfig
import game.bible.passage.Passage
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.util.Date

/**
 * Passage Generation Service Logic
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Service
class GenerationService(
    private val api: BibleApiConfig,
    private val bible: BibleConfig,
    // chat: ChatGptConfig
    private val mapper: ObjectMapper,
    private val restClient: RestClient
) {

    companion object : Log()

    /** Generates a random bible passage */
    fun random(date: Date): Passage {
        val testament = bible.getTestaments()!!.random()

        val division = testament.getDivisions()!!.random()
        val book = division.getBooks()!!.random()
        val bookName = book.getName()!!
        val chapter = "${(1..book.getChapters()!!).random()}"

        val text = fetchText("${book.getName()!!}+$chapter")

        // TODO :: summary from chatGPT


        return Passage(date, bookName, chapter, "", "", 1, text)
    }

    private fun fetchText(passageId: String): String {
        log.debug("Attempting to fetch text for {}", passageId)
        val url = "${api.getBaseUrl()}/$passageId"

        val response = restClient.get()
                        .uri(url).retrieve()
                        .body(String::class.java)

        val jsonNode: JsonNode = mapper.readTree(response)
        return jsonNode["text"].asText()
    }

    // private fub summarise(text)

}