package game.bible.passage.feedback

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

/**
 * Exposes Feedback-related Actions
 * @since 11th October 2025
 */
@RestController
@RequestMapping("/feedback")
class FeedbackController(private val service: FeedbackService) {

    /** Accepts user feedback on generated content */
    @PostMapping
    fun feedback(@RequestBody feedback: Feedback): ResponseEntity<Boolean> {
        log.info { "Feedback request received for passage: ${feedback.passageKey}" }
        val success = service.process(feedback)

        return ResponseEntity.ok(success)
    }
}