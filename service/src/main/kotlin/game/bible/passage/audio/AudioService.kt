package game.bible.passage.audio

import game.bible.passage.generation.GenerationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import game.bible.common.util.resource.S3BucketService

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
    fun retrieveAudio(passageKey: String): ByteArray {

        return bucket.getAudio(passageKey) ?: generateAudio(passageKey)
    }

    /** Handles audio generation and retrieval from storage */
    private fun generateAudio(passageKey: String): ByteArray {
        log.info { "No entry exists for [$passageKey]! Generating audio" }
        val passageAudio = generator.audio(passageKey)

        bucket.uploadAudio(passageKey, passageAudio)
        return passageAudio
    }

}