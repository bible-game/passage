package game.bible.passage.generation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import game.bible.config.model.domain.BibleConfig
import game.bible.config.model.integration.BibleApiConfiguration
import game.bible.config.model.service.PassageConfig
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
    private val api: BibleApiConfiguration,
    private val bible: BibleConfig,
    private val config: PassageConfig,
    private val mapper: ObjectMapper,
    private val restClient: RestClient
) {

    /** Generates a random bible passage */
    fun random(): Passage {
        val random: Int = Random.nextInt(0, 1)
        val testament = if (random == 0) bible.getOld() else bible.getNew()

        // Which book?
        val book: String = testament?.keys!!.random()
        val content = testament[book]

        // Which chapter?
        val chapter: Int = content?.keys?.random()!!
        val verses: Int? = content[chapter]

        // Which starting verse?
        val range: Int = Random.nextInt(config.getMinVerses()!!, config.getMaxVerses()!!)
        val verseStart: Int = Random.nextInt(0, verses!! - range)

        // Which ending verse?
        val verseEnd = verseStart + range

        // Which text?
        val text = fetchText("$book+$chapter:$verseStart-$verseEnd")

        return Passage(book, chapter, verseStart, verseEnd, text)
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