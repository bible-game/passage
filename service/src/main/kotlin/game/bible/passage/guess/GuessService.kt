package game.bible.passage.guess

import game.bible.config.model.domain.BibleConfig
import game.bible.config.model.domain.BibleConfig.BibleTestamentConfig.BibleDivisionConfig.BibleBookConfig
import game.bible.passage.Passage
import game.bible.passage.PassageRepository
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.jvm.optionals.getOrNull
import kotlin.math.floor

/**
 * Guess-related Service Logic
 *
 * @author J. R. Smith
 * @since 21st January 2025
 */
@Service
class GuessService(
    private val bible: BibleConfig,
    private val passageRepository: PassageRepository
) {

    fun evaluate(date: Date, guess: Pair<String, String>): Int {
        val answer = passageRepository.findByDate(date).getOrNull()!! // todo :: if null, throw Ex (code? 400?)

        val correct = (answer.book == guess.first && answer.chapter == guess.second)
        val closeness = if (correct) 100 else calculateCloseness(answer, guess)

        return closeness
    }

    private fun calculateCloseness(answer: Passage, guess: Pair<String, String>): Int {
        val totalVerses = 31_102
        var verseDistance = 0

        val books = mutableListOf<BibleBookConfig>()
        for (testaments in bible.getTestaments()!!) {
            for (division in testaments.getDivisions()!!) {
                for (book in division.getBooks()!!) {
                    books.add(book)
                }
            }
        }

        val guessIndex = books.indexOfFirst { it.getName() == guess.first }
        val answerIndex = books.indexOfFirst { it.getName() == answer.book }

        val lower = if (guessIndex <= answerIndex) guessIndex else answerIndex
        val upper = if (guessIndex <= answerIndex) answerIndex else guessIndex

        val bookList = books.subList(lower, upper)

        // TODO :: need to rethink (start and end chapter / selecting the correct book).. figure it our again
        // add color to UI too...
        for (book in bookList) {
            for (i in 0..<book.getChapters()!!) {
                verseDistance += book.getVerses()!![i]
            }
        }

        return floor((100.0 * (totalVerses - verseDistance)) / totalVerses).toInt()
    }

}