package game.bible.passage.audio

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

/**
 * Exposes Audio-related Actions
 * @since 3rd July 2025
 */
@RestController
@RequestMapping("/audio")
class AudioController(private val service: AudioService) {

    /** Returns the audio for a given passage */
    @GetMapping("/{passageKey}")
    fun getAudio(@PathVariable passageKey: String): ResponseEntity<ByteArray> {
        log.info { "Audio request received for $passageKey" }
        val response: AudioResponse = service.retrieveAudio(passageKey)

        val headers = HttpHeaders().apply {
            contentType = MediaType.parseMediaType(response.contentType)
            contentLength = response.sizeBytes.toLong()
        }

        return ResponseEntity.ok()
            .headers(headers)
            .body(response.audioData)
    }

}