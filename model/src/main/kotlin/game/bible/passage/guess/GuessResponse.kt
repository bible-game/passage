package game.bible.passage.guess

/**
 * Guess response wrapper
 * @since 11th October 2025
 */
data class GuessResponse(
    val distance: Int,
    val percentage: Int,
    val correct: Boolean = distance == 0 && percentage == 100
)