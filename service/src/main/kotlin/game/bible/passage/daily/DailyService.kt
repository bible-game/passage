package game.bible.passage.daily

import game.bible.passage.Passage
import org.springframework.stereotype.Service

/**
 * Daily Passage Service Logic
 *
 * @author J. R. Smith
 * @since 7th December 2024
 */
@Service
class DailyService {

    /** Generates today's bible passage and retrieves it from storage */
    fun retrievePassage(): Passage {
        return Passage("mark", "5")
    }

}