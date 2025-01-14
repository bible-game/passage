package game.bible.passage.generation

import game.bible.config.model.Bible
import game.bible.passage.Passage
import org.springframework.stereotype.Service
import kotlin.random.Random

/**
 * Passage Generation Service Logic
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Service
class GenerationService(private val bible: Bible) {

    // TODO :: load max verses from config
    private final val maxVerses = 4

    /** Generates a random bible passage */
    fun random(): Passage {
        val random: Int = Random.nextInt(0, 1)
        val testament = if (random== 0) bible.getOld() else bible.getNew()

        // Which book?
        val book: String = testament?.keys!!.random()
        val content = testament[book]

        // Which chapter?
        val chapter: Int = content?.keys?.random()!!
        val verses: Int? = content[chapter]

        // Which starting verse?
        val range: Int = Random.nextInt(0, maxVerses)
        val verseStart: Int = Random.nextInt(0, verses!! - range)

        // Which ending verse?
        val verseEnd = verseStart + range

        return Passage(book, chapter, verseStart, verseEnd)
    }

}