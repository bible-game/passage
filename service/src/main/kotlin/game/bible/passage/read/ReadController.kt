package game.bible.passage.read

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

/**
 * Exposes Read-related Actions
 * @since 7th July 2025
 */
@RestController
@RequestMapping("/read")
class ReadController(private val service: ReadService) {

    @GetMapping("/{key}")
    fun getReading(@PathVariable key: String): ResponseEntity<ReadResponse> {
        log.info { "Read request received for $key" }
        val response = service.retrievePassageText(key)
        return ResponseEntity.ok(response)
    }

}