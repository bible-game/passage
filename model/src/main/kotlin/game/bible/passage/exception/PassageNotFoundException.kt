package game.bible.passage.exception

/**
 * Exception thrown when a requested passage, study, or context is not found
 * @since 11th October 2025
 */
class PassageNotFoundException(
    message: String,
    cause: Throwable? = null
) : PassageException(message, cause)