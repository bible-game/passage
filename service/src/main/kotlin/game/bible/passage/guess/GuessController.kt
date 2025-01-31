package game.bible.passage.guess

import game.bible.common.util.log.Log
import game.bible.passage.Passage
import game.bible.passage.daily.DailyService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Date

/**
 * Exposes Guess-related Actions
 *
 * @author J. R. Smith
 * @since 21st January 2025
 */
@RestController
@RequestMapping("/guess")
class GuessController(private val service: GuessService) {

    companion object : Log()

    /** Returns guess 'closeness' for a given passage */
    @GetMapping("/{date}/{book}/{chapter}")
    fun getCloseness(
        @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") date: Date,
        @PathVariable book: String, @PathVariable chapter: String
    ): ResponseEntity<Any> { // Implement custom response object
        return try {
            val guess = Pair(book, chapter)
            val response: Int = service.evaluate(date, guess)
            ResponseEntity.ok(response)

        } catch (e: Exception) {
//            log.error("Some error!") // TODO :: proper err handle
            log.error(e.message)
            ResponseEntity.ok("Some error!")
        }
    }

}