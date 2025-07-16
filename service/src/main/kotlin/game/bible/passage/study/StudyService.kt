package game.bible.passage.study

import game.bible.passage.generation.GenerationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import kotlin.math.pow
import kotlin.math.sqrt

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

    /** Grades a summary for a given passage */
    fun grade(summary: String, passageKey: String): Pair<Int, String> {
        val golden = retrieveStudy(passageKey).goldenSummary

        val userVector = generator.embedding(summary)

        // STEP 1: Break golden into clean, meaningful chunks
        val goldenChunks = golden.split(Regex("(?<=[.!?;])\\s+"))
            .filter { it.split(" ").size >= 3 }  // remove trivial chunks

        // STEP 2: For each chunk, compute similarity to user summary
        val similarities = goldenChunks.map { chunk ->
            val chunkVector = generator.embedding(chunk)
            calcSimilarity(chunkVector, userVector)
        }

        // STEP 3: Grade is now average similarity (stricter)
        val averageSimilarity = similarities.average()
        val grade = Math.round(averageSimilarity * 100).toInt()

        // STEP 4: Identify underrepresented ideas (threshold now stricter)
        val missingChunks = goldenChunks.zip(similarities)
            .filter { (_, sim) -> sim < 0.75 }
            .map { (chunk, _) -> chunk }

        // STEP 5: Simplify missing feedback (first clause)
        val simplified = missingChunks.mapNotNull {
            it.split(Regex("[,.;:]")).firstOrNull()?.trim()
        }.filter { it.isNotBlank() }

        // STEP 6: Build message
        val message = when {
            simplified.isEmpty() -> "Excellent! You captured all the key ideas."
            simplified.size == 1 -> "Nice job! You might also mention: ${simplified[0]}."
            simplified.size in 2..3 -> "Good effort! Consider including ideas like: ${simplified.joinToString("; ")}."
            else -> "You're on the right track. Try to include more of the chapter’s key events or teachings."
        }

        return Pair(grade, message)
    }

    /** Generates and saves a study */
    private fun generateStudy(passageKey: String): Study {
        log.info { "No entry exists for [$passageKey]! Generating study" }
        val study = generator.study(passageKey)

        return repository.save(study)
    }

    /** Calculate cosine similarity between summaries */
    private fun calcSimilarity(vector1: FloatArray, vector2: FloatArray): Double {
        var dot = 0.0; var normA = 0.0; var normB = 0.0

        for (index in vector1.indices) {
            dot += vector1[index] * vector2[index]
            normA += vector1[index].pow(2)
            normB += vector2[index].pow(2)
        }

        return dot / (sqrt(normA) * sqrt(normB))
    }

}