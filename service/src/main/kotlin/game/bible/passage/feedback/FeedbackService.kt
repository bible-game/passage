package game.bible.passage.feedback

import game.bible.passage.generation.GenerationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

/**
 * Passage Feedback Service Logic
 * @since 3rd October 2025
 */
@Service
class FeedbackService(
    private val generationService: GenerationService,
    private val redis: StringRedisTemplate
) {
    /**
     * Processes user feedback on passage context
     */
    fun getFeedback(request: FeedbackRequest): FeedbackResponse {
        log.info { "Received feedback for passage: ${request.passageKey}, sentiment: ${request.feedback}, promptType: ${request.promptType}" }

        if (request.feedback == FeedbackSentiment.NEGATIVE) {
            val newPrompt = generationService.feedbackPrompt(request.passageKey, request.promptType)
            redis.opsForValue().set("precontext:${System.currentTimeMillis()}", newPrompt)
        }

        return FeedbackResponse(
            success = true,
            message = "Thank you for your feedback!"
        )
    }
}