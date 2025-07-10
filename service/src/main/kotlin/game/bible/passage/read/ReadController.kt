package game.bible.passage.read

import game.bible.config.model.integration.BibleApiConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

private val log = KotlinLogging.logger {}

/**
 * Exposes Read-related Actions
 * @since 7th July 2025
 */
@RestController
@RequestMapping("/read")
class ReadController(
    private val api: BibleApiConfig,
    private val restClient: RestClient) {

    // todo :: add AOP rate limiting and retries on 429 to Next.js

    private var cache: Pair<String, String>? = null // todo :: update cache key to be today's value if exists or gen1
    private var default: String? = null

    @GetMapping("/{key}")
    fun getReading(@PathVariable key: String): ResponseEntity<Any> {
        val url = "${api.getBaseUrl()}/$key?single_chapter_book_matching=indifferent"

        if (key == "genesis1") {
            if (default.isNullOrEmpty()) {
                default = restClient.get().uri(url).retrieve().body(String::class.java)!!
            }

            log.info { "Using default!" }
            return ResponseEntity.ok(default)
        }

        if (cache != null && cache!!.first == key) {
            log.info { "Using cache!" }
            return ResponseEntity.ok(cache?.second)
        }

        return try {
            cache = Pair(key, restClient.get().uri(url).retrieve().body(String::class.java)!!)
            ResponseEntity.ok((cache?.second))

        } catch (e: Exception) {
            ResponseEntity.ok("Some error!")
        }
    }

}