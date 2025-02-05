package game.bible.passage.daily

import game.bible.passage.Passage
import game.bible.passage.PassageRepository
import game.bible.passage.generation.GenerationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*


private val log = KotlinLogging.logger {}

/**
 * Daily Passage Service Logic
 *
 * @author J. R. Smith
 * @since 7th December 2024
 */
@Service
class DailyService(
    private val generator: GenerationService,
    private val passageRepository: PassageRepository) {

    private var date: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

    /** Generates a bible passage and retrieves it from storage */
    fun retrievePassage(date: Date): Passage {
        val entry = passageRepository.findByDate(date)

        return if (entry.isPresent) {
            entry.get()

        } else generatePassage(date)
    }

    /** Retrieves paginated list of historic game dates */
    fun retrieveDates(page: Int): List<String> {
        val passages = passageRepository.findAll()
        // TODO :: for a logged in user, return paginated results of all dates (attempted [color?], won [star?], not-attempted [question mark?])
        // for non-logged in user, just return all existing dates (paginate as required)
        // pagination -> pull back a month at a time!

        return passages.map { date.format(it.date) }
    }

    private fun generatePassage(date: Date): Passage {
        log.info { "No entry exists for [$date]! Generating random passage" }
        val randomPassage = generator.random(date)

        return passageRepository.save(randomPassage)
    }

}