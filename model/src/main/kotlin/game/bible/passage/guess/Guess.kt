package game.bible.passage.guess

import java.util.Date

/**
 * Guess request model
 * @since 21st January 2025
 */
data class Guess(
    val date: Date,
    val book: String,
    val chapter: String
)