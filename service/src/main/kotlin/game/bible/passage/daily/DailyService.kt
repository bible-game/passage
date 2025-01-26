package game.bible.passage.daily

import game.bible.common.util.log.Log
import game.bible.passage.Passage
import game.bible.passage.PassageRepository
import game.bible.passage.generation.GenerationService
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
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
    private var date: String = ""
    private var cache: Pair<Passage?, String> = Pair(null, "")

    /** Generates today's bible passage and retrieves it from storage */
    fun retrievePassage(): Passage {
        date = SimpleDateFormat("dd-MM-yyyy").format(Date())

        if (cache.isInvalid()) {
            log.debug("Invalid cache! Searching database for today's entry")
            val entry = passageRepository.findToday()

            val passage = if (entry.isPresent) entry.get() else generatePassage()
            cache = Pair(passage, date)

        }

        return cache.first!!
    }

    private fun generatePassage(): Passage {
        log.debug("No entry exists for today! Generating random passage")
        val randomPassage = generator.random()

        return passageRepository.save(randomPassage)
    }

    private fun Pair<Passage?, String>.isInvalid(): Boolean {
        val valid = this.first == null || this.second != date
        log.debug("Validity of cache determined to be $valid")

        return valid
    }

}