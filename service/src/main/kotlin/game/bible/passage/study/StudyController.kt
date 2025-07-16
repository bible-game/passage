package game.bible.passage.study

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

/**
 * Exposes Study-related Actions
 * @since 3rd July 2025
 */
@RestController
@RequestMapping("/study")
class StudyController(private val service: StudyService) {

    /** Returns the study for a given passage */
    @GetMapping("/{passageKey}")
    fun getStudy(@PathVariable passageKey: String): ResponseEntity<Any> {
        return try {
            log.info { "Study request received for $passageKey" }
            val response: Study = service.retrieveStudy(passageKey)

            ResponseEntity.ok(response)

        } catch (e: Exception) {
            log.error { e.message } // TODO :: implement proper err handle
            ResponseEntity.ok("Some error!")
        }
    }

    /** Returns the summary grade for a given passage */
    @GetMapping("/grade/{passageKey}")
    fun getSummaryGrade(
        @RequestParam("summary") summary: String,
        @PathVariable passageKey: String): ResponseEntity<Any> {
        return try {
            log.info { "Summary grade request received for $passageKey" }
            val response: Pair<Int, String> = service.grade(summary, passageKey)

            ResponseEntity.ok(mapOf(
                "score" to response.first,
                "message" to response.second
            ))
        } catch (e: Exception) {
            log.error { e.message } // TODO :: implement proper err handle
            ResponseEntity.ok("Some error!")
        }
    }

}