package game.bible.passage.exception

/**
 * Base sealed class for all passage-related exceptions
 * @since 11th October 2025
 */
sealed class PassageException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)