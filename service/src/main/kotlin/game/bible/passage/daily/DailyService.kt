package game.bible.passage.daily

import game.bible.common.util.log.Log
import game.bible.passage.Passage
import game.bible.passage.PassageRepository
import game.bible.passage.generation.GenerationService
import org.springframework.stereotype.Service
import java.util.Date

/**
 * Daily Passage Service Logic
 *
 * @author J. R. Smith
 * @since 7th December 2024
 */
@Service
class DailyService(
    private val generator: GenerationService,
    private val passageRepository: PassageRepository
) {

    companion object : Log()

    /** Generates a bible passage and retrieves it from storage */
    fun retrievePassage(date: Date): Passage {
        val entry = passageRepository.findByDate(date)

        return if (entry.isPresent) {
            entry.get()

        } else generatePassage(date)
    }

    private fun generatePassage(date: Date): Passage {
        log.debug("No entry exists for [{}]! Generating random passage", date)
        val randomPassage = generator.random(date)

        return passageRepository.save(randomPassage)
    }

}