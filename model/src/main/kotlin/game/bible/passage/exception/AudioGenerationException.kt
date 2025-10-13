package game.bible.passage.exception

/**
 * Exception thrown when audio generation fails
 * @since 11th October 2025
 */
class AudioGenerationException(
    message: String,
    cause: Throwable? = null
) : PassageException(message, cause)