package game.bible.passage.guess

import game.bible.config.model.domain.BibleConfig
import game.bible.passage.Passage
import game.bible.passage.PassageRepository
import org.springframework.stereotype.Service
import java.util.*
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

    fun evaluate(date: Date, guess: Pair<String, String>): Int {
        val answer = passageRepository.findByDate(date).getOrNull()!! // todo :: if null, throw Ex (code? 400?)

        val correct = (answer.book == guess.first && answer.chapter == guess.second)
        val closeness = if (correct) 100 else calculateCloseness(answer, guess)

        return closeness
    }

    private fun calculateCloseness(answer: Passage, guess: Pair<String, String>): Int {
        return 0
//        val totalVerses = 2196 // 31_102
//        // Question :: should revelation be ~0% not 60%?
//        var verseDistance = 0
//        var altDistance = 0
//
//        val books = bibleConfig.getBooks()!!
//        val guessIndex = books.indexOfFirst { it.getBook() == guess.first }
//        val answerIndex = books.indexOfFirst { it.getBook() == answer.book }
//
//        val lower = if (guessIndex <= answerIndex) guessIndex else answerIndex
//        val upper = if (guessIndex <= answerIndex) answerIndex else guessIndex
//
//        val bookList = books.subList(lower, upper)
//        val altList = books.filterNot { it in bookList }
//
//        for (book in bookList) {
//            book.getChapters()!!.forEach { chapter ->
//                verseDistance += chapter.getVerseEnd()!! - chapter.getVerseStart()!!
//            }
//        }
//
//        // Question :: apply wrap for stars only?
////        for (book in altList) {
////            book.getChapters()!!.forEach { chapter ->
////                altDistance += chapter.getVerseEnd()!! - chapter.getVerseStart()!!
////            }
////        }
////
////        if (altDistance > verseDistance) verseDistance = altDistance
//
//
////        return (verseDistance * 100 / totalVerses)
//
//        return ((totalVerses - verseDistance) * 100 / totalVerses)
    }

}