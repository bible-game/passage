package game.bible.passage.readV2

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import game.bible.config.model.integration.BollsLifeApiConfig
import game.bible.passage.exception.ExternalServiceException
import game.bible.passage.exception.ValidationException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.util.concurrent.ConcurrentHashMap

private val log = KotlinLogging.logger {}

/**
 * Read Service V2 using Bolls.Life API
 * Provides access to multiple Bible versions, books, and verses
 * @since 11th October 2025
 */
@Service
class ReadServiceV2(
  private val api: BollsLifeApiConfig,
  private val restClient: RestClient,
  private val objectMapper: ObjectMapper
) {
    // Cache for books by version
    private val booksCache = ConcurrentHashMap<String, List<BookResponse>>()

    // Cache for verses by version/book/chapter combination
    private val versesCache = ConcurrentHashMap<String, ReadResponseV2>()

    /**
     * Get list of available English Bible versions
     */
    fun getAvailableVersions(): List<VersionInfo> {
        log.info { "Retrieving available Bible versions" }
        return VersionInfo.getEnglishVersions()
    }

    /**
     * Get list of books for a specific Bible version
     */
    fun getBooksForVersion(version: String): List<BookResponse> {
        if (version.isBlank()) {
            throw ValidationException("Version cannot be blank")
        }

        log.info { "getBooksForVersion called for version: $version" }

        // Check cache first
        booksCache[version]?.let {
            log.info { "Using cached books for version: $version" }
            return it
        }

        // Fetch from API
        log.info { "Cache miss, fetching books from API for version: $version" }
        val url = api.getBooksUrl(version)
        val books = fetchBooksFromApi(url, version)

        // Only cache if we got a valid response
        if (books.isNotEmpty()) {
            booksCache[version] = books
            log.info { "Cached ${books.size} books for version: $version" }
        }

        return books
    }

    /**
     * Get a specific book by name from a version
     */
    fun getBookByName(version: String, bookName: String): BookResponse {
        val books = getBooksForVersion(version)
        return books.find { it.name.equals(bookName, ignoreCase = true) }
            ?: throw ValidationException("Book '$bookName' not found in version $version")
    }

    /**
     * Get verses for a specific chapter
     */
    fun getChapterText(
        version: String,
        bookId: String,
        chapter: Int,
        bookName: String? = null
    ): ReadResponseV2 {
        if (version.isBlank()) {
            throw ValidationException("Version cannot be blank")
        }
        if (bookId.isBlank()) {
            throw ValidationException("Book ID cannot be blank")
        }
        if (chapter < 1) {
            throw ValidationException("Chapter must be greater than 0")
        }

        val cacheKey = "$version:$bookId:$chapter"

        // Check cache first
        versesCache[cacheKey]?.let {
            log.info { "Using cached verses for $cacheKey" }
            return it.copy(cached = true)
        }

        // Fetch from API
        val url = api.getTextUrl(version, bookId, chapter)
        val verses = fetchVersesFromApi(url, version, bookId, chapter)

        // Get book name if not provided
        val finalBookName = bookName ?: run {
            val books = getBooksForVersion(version)
            books.find { it.bookId == bookId }?.name ?: bookId
        }

        val response = ReadResponseV2(
            version = version,
            bookId = bookId,
            bookName = finalBookName,
            chapter = chapter,
            verses = verses,
            cached = false
        )

        // Cache the result
        versesCache[cacheKey] = response

        return response
    }

    /**
     * Get verses by book name (convenience method)
     */
    fun getChapterTextByBookName(
        version: String,
        bookName: String,
        chapter: Int
    ): ReadResponseV2 {
        val book = getBookByName(version, bookName)
        return getChapterText(version, book.bookId, chapter, book.name)
    }

    /**
     * Get a specific verse from a chapter
     */
    fun getSpecificVerse(
        version: String,
        bookId: String,
        chapter: Int,
        verseNumber: Int
    ): VerseResponse {
        val chapterText = getChapterText(version, bookId, chapter)
        return chapterText.verses.find { it.verse == verseNumber }
            ?: throw ValidationException("Verse $verseNumber not found in $bookId chapter $chapter")
    }

    /**
     * Get multiple verses from a chapter
     */
    fun getVerseRange(
        version: String,
        bookId: String,
        chapter: Int,
        startVerse: Int,
        endVerse: Int
    ): List<VerseResponse> {
        if (startVerse > endVerse) {
            throw ValidationException("Start verse must be less than or equal to end verse")
        }

        val chapterText = getChapterText(version, bookId, chapter)
        return chapterText.verses.filter { it.verse in startVerse..endVerse }
    }

    /**
     * Clear all caches
     */
    fun clearCache() {
        booksCache.clear()
        versesCache.clear()
        log.info { "All caches cleared" }
    }

    /**
     * Clear cache for a specific version
     */
    fun clearCacheForVersion(version: String) {
        booksCache.remove(version)
        versesCache.keys.removeIf { it.startsWith("$version:") }
        log.info { "Cache cleared for version: $version" }
    }

    private fun fetchBooksFromApi(url: String, version: String): List<BookResponse> {
        log.info { "Fetching books from API for version $version" }
        log.info { "API URL: $url" }

        val response = restClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .scheme("https")
                    .host("bolls.life")
                    .path("/get-books/$version/")
                    .build()
            }
            .retrieve()
            .body(String::class.java)
            ?: throw ExternalServiceException("Bolls.Life API", "Empty response received")

        log.info { "Response received, length: ${response.length}" }
        return objectMapper.readValue(response, object : TypeReference<List<BookResponse>>() {})
    }

    private fun fetchVersesFromApi(
        url: String,
        version: String,
        bookId: String,
        chapter: Int
    ): List<VerseResponse> {
        log.info { "Fetching verses from API for $version/$bookId/$chapter" }
        log.info { "API URL: $url" }

        val response = restClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .scheme("https")
                    .host("bolls.life")
                    .path("/get-text/$version/$bookId/$chapter/")
                    .build()
            }
            .retrieve()
            .body(String::class.java)
            ?: throw ExternalServiceException("Bolls.Life API", "Empty response received")

        log.info { "Response received, length: ${response.length}" }
        return objectMapper.readValue(response, object : TypeReference<List<VerseResponse>>() {})
    }
}