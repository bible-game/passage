package game.bible.passage.feedback

import game.bible.passage.generation.GenerationService
import game.bible.passage.context.PreContextRepository
import game.bible.passage.feedback.PromptType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * Passage Feedback Service Logic
 * @since 3rd October 2025
 */
@Service
class FeedbackService(
    private val generationService: GenerationService,
    private val preContextRepository: PreContextRepository,
    private val redis: StringRedisTemplate
) {
    /**
     * Processes user feedback on passage context
     */
    @Transactional
    fun getFeedback(request: FeedbackRequest): FeedbackResponse {
        log.info { "Received feedback for passage: ${request.passageKey}, sentiment: ${request.feedback}, promptType: ${request.promptType}" }

        if (request.feedback == FeedbackSentiment.NEGATIVE) {
            val newPrompt = generationService.feedbackPrompt(request.comment ?: "I don't like it", request.promptType)
            redis.opsForValue().set("${request.promptType}:${System.currentTimeMillis()}", newPrompt)

            // TODO: Implement for other prompt types
            if (request.promptType == PromptType.PRE_CONTEXT) {
                preContextRepository.deleteByPassageKey(request.passageKey)
            }
        }

        return FeedbackResponse(
            success = true,
            message = "Thank you for your feedback!"
        )
    }
}
