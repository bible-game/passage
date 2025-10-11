package game.bible.passage.read

import game.bible.config.model.integration.BibleApiConfig
import game.bible.passage.exception.ExternalServiceException
import game.bible.passage.exception.ValidationException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

private val log = KotlinLogging.logger {}

/**
 * Read Service Logic
 * @since 11th October 2025
 */
@Service
class ReadService(
    private val api: BibleApiConfig,
    private val restClient: RestClient
) {
    // TODO: add AOP rate limiting and retries on 429 to Next.js
    // TODO: update cache key to be today's value if exists or gen1

    private var cache: Pair<String, String>? = null
    private var default: String? = null

    fun retrievePassageText(passageKey: String): ReadResponse {
        if (passageKey.isBlank()) {
            throw ValidationException("Passage key cannot be blank")
        }

        val url = "${api.getBaseUrl()}/$passageKey?single_chapter_book_matching=indifferent"

        // Handle default passage (genesis1)
        if (passageKey.lowercase() == "genesis1") {
            if (default.isNullOrEmpty()) {
                default = fetchFromApi(url)
            }
            log.info { "Using default passage!" }
            return ReadResponse(
                passageKey = passageKey,
                text = default!!,
                cached = true
            )
        }

        // Check cache
        if (cache != null && cache!!.first == passageKey) {
            log.info { "Using cached passage!" }
            return ReadResponse(
                passageKey = passageKey,
                text = cache!!.second,
                cached = true
            )
        }

        // Fetch from API
        val text = fetchFromApi(url)
        cache = Pair(passageKey, text)

        return ReadResponse(
            passageKey = passageKey,
            text = text,
            cached = false
        )
    }

    private fun fetchFromApi(url: String): String {
        return try {
            restClient.get()
                .uri(url)
                .retrieve()
                .body(String::class.java)
                ?: throw ExternalServiceException("Bible API", "Empty response received")
        } catch (e: Exception) {
            log.error(e) { "Failed to fetch from Bible API: $url" }
            throw ExternalServiceException("Bible API", "Failed to fetch passage text", e)
        }
    }
}
