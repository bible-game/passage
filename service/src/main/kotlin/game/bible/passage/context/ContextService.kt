package game.bible.passage.context

import game.bible.passage.exception.ValidationException
import game.bible.passage.generation.GenerationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

/**
 * Reading Context Service Logic
 * @since 3rd July 2025
 */
@Service
class ContextService(
    private val generator: GenerationService,
    private val preContextRepository: PreContextRepository,
    private val postContextRepository: PostContextRepository) {

    /** Generates the pre-context for a given passage and retrieves it from storage */
    fun retrievePreContext(passageKey: String): PreContextResponse {
        if (passageKey.isBlank()) {
            throw ValidationException("Passage key cannot be blank")
        }

        val entry = preContextRepository.findByPassageKey(passageKey)
        val preContext = if (entry.isPresent) {
            entry.get()
        } else {
            generatePreContext(passageKey)
        }

        return PreContextResponse(
            passageKey = preContext.passageKey,
            text = preContext.text
        )
    }

    /** Generates the post-context for a given passage and retrieves it from storage */
    fun retrievePostContext(passageKey: String): PostContextResponse {
        if (passageKey.isBlank()) {
            throw ValidationException("Passage key cannot be blank")
        }

        val entry = postContextRepository.findByPassageKey(passageKey)
        val postContext = if (entry.isPresent) {
            entry.get()
        } else {
            generatePostContext(passageKey)
        }

        return PostContextResponse(
            passageKey = postContext.passageKey,
            text = postContext.text
        )
    }

    private fun generatePreContext(passageKey: String): PreContext {
        log.info { "No entry exists for [$passageKey]! Generating pre-context" }
        val preContext = generator.preContext(passageKey)

        return preContextRepository.save(preContext)
    }

    private fun generatePostContext(passageKey: String): PostContext {
        log.info { "No entry exists for [$passageKey]! Generating post-context" }
        val postContext = generator.postContext(passageKey)

        return postContextRepository.save(postContext)
    }

}