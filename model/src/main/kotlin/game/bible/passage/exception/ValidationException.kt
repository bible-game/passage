package game.bible.passage.exception

/**
 * Exception thrown when input validation fails
 * @since 11th October 2025
 */
class ValidationException(
    message: String,
    cause: Throwable? = null
) : PassageException(message, cause)