package game.bible.passage.read

/**
 * Read response wrapper for bible text
 * @since 11th October 2025
 */
data class ReadResponse(
    val passageKey: String,
    val text: String,
    val cached: Boolean = false
)