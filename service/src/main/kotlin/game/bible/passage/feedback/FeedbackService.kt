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

        //TODO: Using the feedback data, we ask for an update prompt to be generated
      // TODO: We will do this every time for now and later we will aggregate feedback and do it once in a while
        //TODO: Store the new prompt in redis for future use for now

        return FeedbackResponse(
            success = true,
            message = "Thank you for your feedback!"
        )
    }
}