package game.bible.passage.context

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

/**
 * Exposes Reading Context-related Actions
 * @since 3rd July 2025
 */
@RestController
@RequestMapping("/context")
class ContextController(private val service: ContextService) {

    /** Returns the context leading up to a given passage */
    @GetMapping("/before/{passageKey}")
    fun getPreContext(@PathVariable passageKey: String): ResponseEntity<PreContext> {
        log.info { "Pre-context request received for $passageKey" }
        val response = service.retrievePreContext(passageKey)

        return ResponseEntity.ok(response)
    }

    /** Returns the context after a given passage */
    @GetMapping("/after/{passageKey}")
    fun getPostContext(@PathVariable passageKey: String): ResponseEntity<PostContext> {
        log.info { "Post-context request received for $passageKey" }
        val response = service.retrievePostContext(passageKey)

        return ResponseEntity.ok(response)
    }

}