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

    /**
     * Receives user feedback on passage context
     */
    @PostMapping
    fun postFeedback(@RequestBody request: FeedbackRequest): ResponseEntity<FeedbackResponse> {
        log.info { "Feedback request received for passage: ${request.passageKey}" }
        val response = service.getFeedback(request)

        return ResponseEntity.ok(response)
    }
}