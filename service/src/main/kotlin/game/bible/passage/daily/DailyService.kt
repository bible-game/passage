package game.bible.passage.daily

import game.bible.passage.Passage
import game.bible.passage.PassageRepository
import game.bible.passage.generation.GenerationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.Date

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

    /** Generates a bible passage and retrieves it from storage */
    fun retrievePassage(date: Date): Passage {
        val entry = passageRepository.findByDate(date)

        return if (entry.isPresent) {
            entry.get()

        } else generatePassage(date)
    }

    /** Retrieves paginated list of historic game dates */
    fun retrieveDates(page: Int): List<Date> {
        val passages = passageRepository.findAll()
        // TODO :: just return last and any in range (today) that are missing... no need for pagination
        // min ""
        // missing: ["", ...]
        // max: <<TODAY>>

        return passages.map { it.date }
    }

    private fun generatePassage(date: Date): Passage {
        log.info { "No entry exists for [$date]! Generating random passage" }
        val randomPassage = generator.random(date)

        return passageRepository.save(randomPassage)
    }

}