package game.bible.passage.exception

/**
 * Exception thrown when external service calls fail (Bible API, OpenAI, AWS S3, etc.)
 * @since 11th October 2025
 */
class ExternalServiceException(
    val serviceName: String,
    message: String,
    cause: Throwable? = null
) : PassageException("$serviceName: $message", cause)