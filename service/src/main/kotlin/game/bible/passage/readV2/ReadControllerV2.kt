package game.bible.passage.readV2

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val log = KotlinLogging.logger {}

/**
 * Exposes Read V2 Actions using Bolls.Life API
 * @since 11th October 2025
 */
@RestController
@RequestMapping("/v2/read")
class ReadControllerV2(private val service: ReadServiceV2) {

    /**
     * GET /v2/read/versions
     * Get list of available Bible versions
     */
    @GetMapping("/versions")
    fun getVersions(): ResponseEntity<List<VersionInfo>> {
        log.info { "Request received for Bible versions" }
        val versions = service.getAvailableVersions()
        return ResponseEntity.ok(versions)
    }

    /**
     * GET /v2/read/books/{version}
     * Get list of books for a specific version
     */
    @GetMapping("/books/{version}")
    fun getBooks(@PathVariable version: String): ResponseEntity<List<BookResponse>> {
        log.info { "Request received for books in version: $version" }
        val books = service.getBooksForVersion(version)
        return ResponseEntity.ok(books)
    }

    /**
     * GET /v2/read/book/{version}/{bookName}
     * Get a specific book by name from a version
     */
    @GetMapping("/book/{version}/{bookName}")
    fun getBook(
        @PathVariable version: String,
        @PathVariable bookName: String
    ): ResponseEntity<BookResponse> {
        log.info { "Request received for book: $bookName in version: $version" }
        val book = service.getBookByName(version, bookName)
        return ResponseEntity.ok(book)
    }

    /**
     * GET /v2/read/chapter/{version}/{bookId}/{chapter}
     * Get verses for a specific chapter using book ID
     */
    @GetMapping("/chapter/{version}/{bookId}/{chapter}")
    fun getChapter(
        @PathVariable version: String,
        @PathVariable bookId: String,
        @PathVariable chapter: Int
    ): ResponseEntity<ReadResponseV2> {
        log.info { "Request received for $version/$bookId/$chapter" }
        val response = service.getChapterText(version, bookId, chapter)
        return ResponseEntity.ok(response)
    }

    /**
     * GET /v2/read/chapter-by-name/{version}/{bookName}/{chapter}
     * Get verses for a specific chapter using book name
     */
    @GetMapping("/chapter-by-name/{version}/{bookName}/{chapter}")
    fun getChapterByName(
        @PathVariable version: String,
        @PathVariable bookName: String,
        @PathVariable chapter: Int
    ): ResponseEntity<ReadResponseV2> {
        log.info { "Request received for $version/$bookName/$chapter" }
        val response = service.getChapterTextByBookName(version, bookName, chapter)
        return ResponseEntity.ok(response)
    }

    /**
     * GET /v2/read/verse/{version}/{bookId}/{chapter}/{verse}
     * Get a specific verse
     */
    @GetMapping("/verse/{version}/{bookId}/{chapter}/{verse}")
    fun getVerse(
        @PathVariable version: String,
        @PathVariable bookId: String,
        @PathVariable chapter: Int,
        @PathVariable verse: Int
    ): ResponseEntity<VerseResponse> {
        log.info { "Request received for verse: $version/$bookId/$chapter:$verse" }
        val response = service.getSpecificVerse(version, bookId, chapter, verse)
        return ResponseEntity.ok(response)
    }

    /**
     * GET /v2/read/verse-range/{version}/{bookId}/{chapter}?start={start}&end={end}
     * Get a range of verses
     */
    @GetMapping("/verse-range/{version}/{bookId}/{chapter}")
    fun getVerseRange(
        @PathVariable version: String,
        @PathVariable bookId: String,
        @PathVariable chapter: Int,
        @RequestParam start: Int,
        @RequestParam end: Int
    ): ResponseEntity<List<VerseResponse>> {
        log.info { "Request received for verse range: $version/$bookId/$chapter:$start-$end" }
        val verses = service.getVerseRange(version, bookId, chapter, start, end)
        return ResponseEntity.ok(verses)
    }

    /**
     * DELETE /v2/read/cache
     * Clear all caches
     */
    @DeleteMapping("/cache")
    fun clearCache(): ResponseEntity<Map<String, String>> {
        log.info { "Request received to clear all caches" }
        service.clearCache()
        return ResponseEntity.ok(mapOf("message" to "All caches cleared successfully"))
    }

    /**
     * DELETE /v2/read/cache/{version}
     * Clear cache for a specific version
     */
    @DeleteMapping("/cache/{version}")
    fun clearCacheForVersion(@PathVariable version: String): ResponseEntity<Map<String, String>> {
        log.info { "Request received to clear cache for version: $version" }
        service.clearCacheForVersion(version)
        return ResponseEntity.ok(mapOf("message" to "Cache cleared for version: $version"))
    }
}