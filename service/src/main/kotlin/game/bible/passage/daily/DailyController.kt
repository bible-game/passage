package game.bible.passage.daily

import game.bible.passage.Passage
import game.bible.common.util.log.Log
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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

    /** Returns today's bible passage */
    @GetMapping
    fun getPassage(): ResponseEntity<Any> {
        return try {
            val response: Passage = service.retrievePassage()
            ResponseEntity.ok(response)

        } catch (e: Exception) {
            log.error("Some error!")
            ResponseEntity.ok("Some error!")
        }
    }

}