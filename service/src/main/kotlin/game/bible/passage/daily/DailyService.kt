package game.bible.passage.daily

import game.bible.common.util.log.Log
import game.bible.config.model.Bible
import game.bible.passage.Passage
import game.bible.passage.PassageRepository
import game.bible.passage.generation.GenerationService
import org.springframework.stereotype.Service

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

    companion object : Log()
    private var cache: Passage? = null

    /** Generates today's bible passage and retrieves it from storage */
    fun retrievePassage(): Passage {
        if (cache == null) {
            log.debug("No cached passage! Searching database for today's entry")
            val today = passageRepository.findToday()

            cache = if (today.isPresent) today.get()
                        else generatePassage()
        }

        return cache!!
    }

    private fun generatePassage(): Passage {
        log.debug("No entry exists for today! Generating random passage")
        val randomPassage = generator.random()

        return passageRepository.save(randomPassage)
    }

}