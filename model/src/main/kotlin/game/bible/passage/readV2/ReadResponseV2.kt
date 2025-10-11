package game.bible.passage.readV2

/**
 * Read response wrapper for Bible text from bolls.life API
 * @since 11th October 2025
 */
data class ReadResponseV2(
    val version: String,
    val bookId: String,
    val bookName: String,
    val chapter: Int,
    val verses: List<VerseResponse>,
    val cached: Boolean = false
)