package game.bible.passage.daily

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date

private val log = KotlinLogging.logger {}

/**
 * Exposes Daily Passage-related Actions
 *
 * @since 7th December 2024
 */
@RestController
@RequestMapping("/daily")
class DailyController(private val service: DailyService) {

    /** Returns the bible passage for a given date */
    @GetMapping("/{date}")
    fun getPassage(
        @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") date: Date): ResponseEntity<Any> { // TODO :: implement custom response object
        return try {
            log.info { "Passage request received for $date" }

            val response = service.retrievePassage(date)
            ResponseEntity.ok(response)

        } catch (e: Exception) {
            log.error { e.message } // TODO :: implement proper err handle
            ResponseEntity.ok("Some error!")
        }
    }

    /** Returns the dates of historic daily passages */
    @GetMapping("/history")
    fun getHistory(@RequestParam(defaultValue = "0") page: Int): ResponseEntity<Any> {
        return try {
            log.info { "Request for previous dates [page: $page]" }

            val response = service.retrieveDates(page)
            ResponseEntity.ok(response)

        } catch (e: Exception) {
            log.error { e.message } // TODO :: implement proper err handle
            ResponseEntity.ok("Some error!")
        }
    }

}