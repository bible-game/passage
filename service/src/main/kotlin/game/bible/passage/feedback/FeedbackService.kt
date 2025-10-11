package game.bible.passage.feedback

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

/**
 * Passage Feedback Service Logic
 * @since 3rd October 2025
 */
@Service
class FeedbackService {

    /**
     * Processes user feedback on passage context
     */
    fun getFeedback(request: FeedbackRequest): FeedbackResponse {
        log.info { "Received feedback for passage: ${request.passageKey}, sentiment: ${request.feedback}, context: ${request.context}" }

        // TODO: Store feedback in database
        // TODO: If negative feedback accumulates, trigger prompt optimization
        // TODO: Track feedback metrics for analytics

        return FeedbackResponse(
            success = true,
            message = "Thank you for your feedback!"
        )
    }
}

/**
 * Feedback request data
 */
data class FeedbackRequest(
    val passageKey: String,
    val feedback: FeedbackSentiment,
    val context: ContextType,
    val comment: String? = null
)

/**
 * Feedback sentiment enum
 */
enum class FeedbackSentiment {
    POSITIVE,
    NEGATIVE
}

/**
 * Context type enum
 */
enum class ContextType {
    BEFORE,
    AFTER
}

/**
 * Feedback response data
 */
data class FeedbackResponse(
    val success: Boolean,
    val message: String
)