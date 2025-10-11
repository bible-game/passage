package game.bible.passage.exception

import java.time.Instant

/**
 * Standardized error response model
 * @since 11th October 2025
 */
data class ErrorResponse(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)