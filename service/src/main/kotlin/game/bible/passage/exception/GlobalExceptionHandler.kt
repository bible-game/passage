package game.bible.passage.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

private val log = KotlinLogging.logger {}

/**
 * Global exception handler for all REST controllers
 * @since 11th October 2025
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(PassageNotFoundException::class)
    fun handlePassageNotFound(
        ex: PassageNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.warn { "Resource not found: ${ex.message}" }

        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Not Found",
            message = ex.message ?: "The requested resource was not found",
            path = request.requestURI
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidation(
        ex: ValidationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.warn { "Validation error: ${ex.message}" }

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.message ?: "Invalid input provided",
            path = request.requestURI
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(ExternalServiceException::class)
    fun handleExternalService(
        ex: ExternalServiceException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error(ex) { "External service error: ${ex.message}" }

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_GATEWAY.value(),
            error = "Service Unavailable",
            message = ex.message ?: "External service temporarily unavailable",
            path = request.requestURI
        )

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse)
    }

    @ExceptionHandler(AudioGenerationException::class)
    fun handleAudioGeneration(
        ex: AudioGenerationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Audio generation error: ${ex.message}" }

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Audio Generation Failed",
            message = ex.message ?: "Failed to generate audio for the passage",
            path = request.requestURI
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(StorageException::class)
    fun handleStorage(
        ex: StorageException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Storage error: ${ex.message}" }

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Storage Error",
            message = ex.message ?: "Failed to access storage",
            path = request.requestURI
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.warn { "Type mismatch: ${ex.message}" }

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = "Invalid parameter type: ${ex.name}",
            path = request.requestURI
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Unexpected error: ${ex.message}" }

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = "An unexpected error occurred",
            path = request.requestURI
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}