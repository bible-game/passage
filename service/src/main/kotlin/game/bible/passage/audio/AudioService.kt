package game.bible.passage.audio

import game.bible.common.util.resource.S3BucketService
import game.bible.passage.exception.ValidationException
import game.bible.passage.generation.GenerationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

/**
 * Passage Audio Service Logic
 * @since 3rd July 2025
 */
@Service
class AudioService(
    private val bucket: S3BucketService,
    private val generator: GenerationService) {

    /** Retrieves the audio for a given passage */
    fun retrieveAudio(passageKey: String): AudioResponse {
        if (passageKey.isBlank()) {
            throw ValidationException("Passage key cannot be blank")
        }

        val audioData = bucket.getAudio(passageKey) ?: generateAudio(passageKey)

        return AudioResponse(
            passageKey = passageKey,
            audioData = audioData
        )
    }

    /** Handles audio generation and retrieval from storage */
    private fun generateAudio(passageKey: String): ByteArray {
        log.info { "No entry exists for [$passageKey]! Generating audio" }
        val passageAudio = generator.audio(passageKey)

        bucket.uploadAudio(passageKey, passageAudio)
        return passageAudio
    }

}