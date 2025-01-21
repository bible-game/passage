package game.bible.passage.generation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import game.bible.config.model.domain.BibleConfig
import game.bible.config.model.integration.BibleApiConfig
import game.bible.passage.Passage
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import kotlin.random.Random

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
    private val mapper: ObjectMapper,
    private val restClient: RestClient
) {

    /** Generates a random bible passage */
    fun random(): Passage {
        // Which book?
        val random = bible.getBooks()!!.random()
        val book = random.getBook()!!

        // Which passage?
        val passage = random.getChapters()!!.random()
        val chapter = passage.getChapter()!!
        val title = passage.getTitle()!!
        val verseStart = passage.getVerseStart()!!
        val verseEnd = passage.getVerseEnd()!!

        val text = fetchText("$book+$chapter:$verseStart-$verseEnd")

        return Passage(book, chapter, title, verseStart, verseEnd, text)
    }

    /** Fetches the text for generated passage */
    fun fetchText(passageId: String): String {
        val url = "${api.getBaseUrl()}/$passageId"

        val response = restClient.get()
                        .uri(url).retrieve()
                        .body(String::class.java)

        val jsonNode: JsonNode = mapper.readTree(response)
        return jsonNode["text"].asText()
    }

}