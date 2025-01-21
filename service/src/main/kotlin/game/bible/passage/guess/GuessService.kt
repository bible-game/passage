package game.bible.passage.guess

import game.bible.config.model.domain.BibleConfig
import game.bible.passage.PassageRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

/**
 * Guess-related Service Logic
 *
 * @author J. R. Smith
 * @since 21st January 2025
 */
@Service
class GuessService(
    private val bibleConfig: BibleConfig,
    private val passageRepository: PassageRepository
) {

    fun evaluate(guess: Pair<String, String>): Int {
        val today = passageRepository.findToday().getOrNull()!! // todo :: if null, throw Ex (code? 400?)

        val correct = (today.book == guess.first && today.title == guess.second)
        val closeness = if (correct) 100 else calculateCloseness(guess)

        return closeness
    }

    private fun calculateCloseness(guess: Pair<String, String>): Int { // todo :: flesh this out later...
        val totalVerses = 300 // 31_102
        var verseDistance = 0

        // note :: starting with a simple case -> guess < answer (sum verses in the config that are in between)
        val book = bibleConfig.getBooks()!!.filter { it.getBook() == guess.first }[0]
        val passage = book.getChapters()!!.filter { it.getTitle() == guess.second }[0]

        verseDistance += passage.getVerseEnd()!!

        return (verseDistance * 100 / totalVerses)
    }

}