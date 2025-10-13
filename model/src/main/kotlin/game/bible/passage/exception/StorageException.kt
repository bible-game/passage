package game.bible.passage.exception

/**
 * Exception thrown when database or storage operations fail
 * @since 11th October 2025
 */
class StorageException(
    message: String,
    cause: Throwable? = null
) : PassageException(message, cause)