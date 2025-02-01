package game.bible.passage.guess

import game.bible.config.model.domain.BibleConfig
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
    bible: BibleConfig,
    private val passageRepository: PassageRepository
) {

    private var verseMap: MutableMap<String, Int> = mutableMapOf()

    init {
        for (test in bible.getTestaments()!!) {
            for (div in test.getDivisions()!!) {
                for (book in div.getBooks()!!) {
                    val verses = book.getVerses()!!
                    for (chapter in 1..book.getChapters()!!) {
                        verseMap["${book.getName()}${chapter}"] = verses[chapter - 1]
                    }
                }
            }
        }
    }

    fun evaluate(date: Date, guess: Pair<String, String>): Int {
        val answer = passageRepository.findByDate(date).getOrNull()!!
        // todo :: if null, throw Ex (code? 400?)

        val correct = (answer.book == guess.first && answer.chapter == guess.second)
        val closeness = if (correct) 0 else calculateCloseness(answer, guess)

        return closeness
    }

    private fun calculateCloseness(answer: Passage, guess: Pair<String, String>): Int {
        var verseDistance = 0

        val guessIndex = verseMap.keys.indexOf("${guess.first}${guess.second}")
        val answerIndex = verseMap.keys.indexOf("${answer.book}${answer.chapter}")

        val lower = if (guessIndex <= answerIndex) guessIndex else answerIndex
        val upper = if (guessIndex <= answerIndex) answerIndex else guessIndex

        val chapters = verseMap.keys.toList().subList(lower, upper)

        for (chapter in chapters) {
            verseDistance += verseMap[chapter]!!
        }

        return verseDistance
    }

}