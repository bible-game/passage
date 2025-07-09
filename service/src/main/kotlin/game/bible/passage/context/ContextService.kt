package game.bible.passage.context

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
    fun retrievePreContext(passageKey: String): PreContext {
        val entry = preContextRepository.findByPassageKey(passageKey)

        return if (entry.isPresent) {
            entry.get()

        } else generatePreContext(passageKey)
    }

    /** Generates the post-context for a given passage and retrieves it from storage */
    fun retrievePostContext(passageKey: String): PostContext {
        val entry = postContextRepository.findByPassageKey(passageKey)

        return if (entry.isPresent) {
            entry.get()

        } else generatePostContext(passageKey)
    }

    private fun generatePreContext(passageKey: String): PreContext {
        log.info { "No entry exists for [$passageKey]! Generating pre-context" }
        val preContext = generator.preContext(passageKey)

        return preContextRepository.save(preContext)
    }

    private fun generatePostContext(passageKey: String): PostContext {
        log.info { "No entry exists for [$passageKey]! Generating post-context" }
        val preContext = generator.postContext(passageKey)

        return postContextRepository.save(preContext)
    }

}