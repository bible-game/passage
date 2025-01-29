package game.bible.passage.daily

import game.bible.passage.Passage
import game.bible.common.util.log.Log
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Date

/**
 * Exposes Daily Passage-related Actions
 *
 * @author J. R. Smith
 * @since 7th December 2024
 */
@RestController
@RequestMapping("/daily")
class DailyController(private val service: DailyService) {

    companion object : Log()

    /** Returns the bible passage for a given date */
    @GetMapping("/{date}")
    fun getPassage(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") date: Date): ResponseEntity<Any> {
        // TODO :: implement custom response object
        return try {
            val response: Passage = service.retrievePassage(date)
            ResponseEntity.ok(response)

        } catch (e: Exception) {
//            log.error("Some error!") // TODO :: implement proper err handle
            log.error(e.message)
            ResponseEntity.ok("Some error!")
        }
    }

}