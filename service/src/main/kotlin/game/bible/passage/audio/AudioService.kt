package game.bible.passage.audio

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
//    private val bucket: S3BucketService,
    private val generator: GenerationService) {

    /** Generates the audio for a given passage and retrieves it from storage */
    fun retrieveAudio(passageKey: String): ByteArray { // fixme
        return generateAudio(passageKey)
//        val entry = bucket.findAudio(passageKey)
//
//        return if (entry.isPresent) {
//            entry.get()
//
//        } else generateAudio(passageKey)
    }

    private fun generateAudio(passageKey: String): ByteArray {
        log.info { "No entry exists for [$passageKey]! Generating audio" }
        val passageAudio = generator.audio(passageKey)

//        return bucket.saveAudio(passageAudio)
        return passageAudio
    }

}