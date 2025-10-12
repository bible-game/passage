package game.bible.passage.feedback

import game.bible.passage.generation.GenerationService
import game.bible.passage.context.PreContextRepository
import game.bible.passage.feedback.Prompt.PRE_CONTEXT
import game.bible.passage.feedback.Sentiment.NEGATIVE
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * User Feedback Service Logic
 * @since 3rd October 2025
 */
@Service
class FeedbackService(
    private val redis: StringRedisTemplate,
    private val generationService: GenerationService,
    private val preContextRepository: PreContextRepository
) {
    /** Processes user feedback on generated content */
    @Transactional
    fun process(feedback: Feedback): Boolean {
        log.info { "Received [${feedback.sentiment}] feedback on [${feedback.prompt}] generation type" }

        if (feedback.sentiment == NEGATIVE) {
            val comment = feedback.comment ?: "I don't like it"
            val newPrompt = generationService.feedbackPrompt(comment, feedback.prompt)
            redis.opsForValue().set("${feedback.prompt}:${System.currentTimeMillis()}", newPrompt)

            // TODO: Implement for other prompt types
            if (feedback.prompt == PRE_CONTEXT)
                preContextRepository.deleteByPassageKey(feedback.passageKey)
        }

        return true
    }
}
