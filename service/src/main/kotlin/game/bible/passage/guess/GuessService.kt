package game.bible.passage.guess

import game.bible.config.model.domain.BibleConfig
import game.bible.passage.Passage
import game.bible.passage.PassageRepository
import game.bible.passage.exception.PassageNotFoundException
import game.bible.passage.exception.ValidationException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull
import kotlin.math.floor

private val log = KotlinLogging.logger {}

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

    fun evaluate(guess: Guess): GuessResponse {
        if (guess.book.isBlank()) {
            throw ValidationException("Book name cannot be blank")
        }
        if (guess.chapter.isBlank()) {
            throw ValidationException("Chapter cannot be blank")
        }

        val answer = passageRepository.findByDate(guess.date).getOrNull()
            ?: throw PassageNotFoundException("No passage found for date: ${guess.date}")

        val correct = (answer.book == guess.book && answer.chapter == guess.chapter)
        val closeness = if (correct) {
            Closeness(0, 100)
        } else {
            calculateCloseness(answer, guess)
        }

        return GuessResponse(
            distance = closeness.distance,
            percentage = closeness.percentage,
            correct = correct
        )
    }

    private fun calculateCloseness(answer: Passage, guess: Guess): Closeness {
        val totalVerses = 31_102
        var verseDistance = 0

        val guessIndex = verseMap.keys.indexOf("${guess.book}${guess.chapter}")
        val answerIndex = verseMap.keys.indexOf("${answer.book}${answer.chapter}")

        if (guessIndex == -1) {
            throw ValidationException("Invalid book/chapter: ${guess.book}${guess.chapter}")
        }

        val lower = if (guessIndex <= answerIndex) guessIndex else answerIndex
        val upper = if (guessIndex <= answerIndex) answerIndex else guessIndex

        val chapters = verseMap.keys.toList().subList(lower, upper)

        for (chapter in chapters) {
            verseDistance += verseMap[chapter]!!
        }

        val sign  = if (guessIndex < answerIndex) -1 else 1

        val percentage = 100.0 * (totalVerses - verseDistance) / totalVerses
        return Closeness(sign * verseDistance, floor(percentage).toInt())
    }

}