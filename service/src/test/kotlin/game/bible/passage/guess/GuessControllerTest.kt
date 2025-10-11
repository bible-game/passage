package game.bible.passage.guess

import game.bible.passage.exception.PassageNotFoundException
import game.bible.passage.exception.ValidationException
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Date

/**
 * Unit tests for GuessController
 * @since 11th October 2025
 */
@WebMvcTest(controllers = [GuessController::class])
@AutoConfigureMockMvc(addFilters = false)
class GuessControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var guessService: GuessService

    @Test
    fun `should return closeness successfully for correct guess`() {
        // Given
        val date = Date()
        val guess = Guess(date, "Revelation", "18")
        val response = GuessResponse(
            distance = 0,
            percentage = 100,
            correct = true
        )

        `when`(guessService.evaluate(guess)).thenReturn(response)

        // When & Then
        mockMvc.perform(get("/guess/2025-06-26/Revelation/18"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.distance").value(0))
            .andExpect(jsonPath("$.percentage").value(100))
            .andExpect(jsonPath("$.correct").value(true))
    }

    @Test
    fun `should return closeness for incorrect guess`() {
        // Given
        val date = Date()
        val guess = Guess(date, "Genesis", "1")
        val response = GuessResponse(
            distance = -15000,
            percentage = 52,
            correct = false
        )

        `when`(guessService.evaluate(guess)).thenReturn(response)

        // When & Then
        mockMvc.perform(get("/guess/2025-06-26/Genesis/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.distance").value(-15000))
            .andExpect(jsonPath("$.percentage").value(52))
            .andExpect(jsonPath("$.correct").value(false))
    }

    @Test
    fun `should return 400 when book is blank`() {
        // Given
        val date = Date()
        val guess = Guess(date, "   ", "1")
        `when`(guessService.evaluate(guess))
            .thenThrow(ValidationException("Book name cannot be blank"))

        // When & Then
        mockMvc.perform(get("/guess/2025-06-26/%20%20%20/1"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Book name cannot be blank"))
    }

    @Test
    fun `should return 404 when no passage found for date`() {
        // Given
        val date = Date()
        val guess = Guess(date, "Genesis", "1")
        `when`(guessService.evaluate(guess))
            .thenThrow(PassageNotFoundException("No passage found for date: $date"))

        // When & Then
        mockMvc.perform(get("/guess/2025-12-31/Genesis/1"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
    }

    @Test
    fun `should return 400 for invalid book chapter combination`() {
        // Given
        val date = Date()
        val guess = Guess(date, "InvalidBook", "999")
        `when`(guessService.evaluate(guess))
            .thenThrow(ValidationException("Invalid book/chapter: InvalidBook999"))

        // When & Then
        mockMvc.perform(get("/guess/2025-06-26/InvalidBook/999"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Invalid book/chapter: InvalidBook999"))
    }
}