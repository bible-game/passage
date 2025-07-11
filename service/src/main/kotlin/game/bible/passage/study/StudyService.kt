package game.bible.passage.study

import game.bible.passage.generation.GenerationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

/**
 * Passage Audio Service Logic
 * @since 3rd July 2025
 */
@Service
class StudyService(
    private val repository: StudyRepository,
    private val generator: GenerationService) {

    /** Retrieves the study for a given passage */
    fun retrieveStudy(passageKey: String): Study {
        val entry = repository.findByPassageKey(passageKey)

        return if (entry.isPresent) {
            entry.get()

        } else generateStudy(passageKey)
    }

    /** Generates and saves a study */
    private fun generateStudy(passageKey: String): Study {
        log.info { "No entry exists for [$passageKey]! Generating study" }
        val study = generator.study(passageKey)

        return repository.save(study)
    }

}