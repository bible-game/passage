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
        val random: Int = Random.nextInt(0, 1)
        val testament = if (random == 0) bible.getOld() else bible.getOld() // .getNew()

        // Which book?
        val book: String = testament?.keys!!.random()

        // Which chapter?
        val chapter = testament[book]!!.keys.random()
        val passages = testament[book]?.get(chapter)

        // Which verses?
        val summary = passages!!.keys.random()
        val verses = passages[summary]!!

        // Which text?
        val verseStart = verses[0]; val verseEnd = verses[1]
        val text = fetchText("$book+$chapter:$verseStart-$verseEnd")

        return Passage(book, chapter, summary, verseStart, verseEnd, text)
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