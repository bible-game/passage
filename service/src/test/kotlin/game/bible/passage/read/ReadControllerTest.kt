package game.bible.passage.read

import game.bible.passage.exception.ExternalServiceException
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

/**
 * Unit tests for ReadController
 * @since 11th October 2025
 */
@WebMvcTest(controllers = [ReadController::class])
@AutoConfigureMockMvc(addFilters = false)
class ReadControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var readService: ReadService

    @Test
    fun `should return passage text successfully`() {
        // Given
        val passageKey = "3John1"
        val response = ReadResponse(
            passageKey = passageKey,
            text = "John to the beloved Gaius...",
            cached = false
        )

        `when`(readService.retrievePassageText(passageKey)).thenReturn(response)

        // When & Then
        mockMvc.perform(get("/read/$passageKey"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.passageKey").value(passageKey))
            .andExpect(jsonPath("$.text").value("John to the beloved Gaius..."))
            .andExpect(jsonPath("$.cached").value(false))
    }

    @Test
    fun `should return cached passage text`() {
        // Given
        val passageKey = "genesis1"
        val response = ReadResponse(
            passageKey = passageKey,
            text = "In the beginning God created...",
            cached = true
        )

        `when`(readService.retrievePassageText(passageKey)).thenReturn(response)

        // When & Then
        mockMvc.perform(get("/read/$passageKey"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.passageKey").value(passageKey))
            .andExpect(jsonPath("$.cached").value(true))
    }

    @Test
    fun `should return 400 when passage key is blank`() {
        // Given
        val passageKey = "   "
        `when`(readService.retrievePassageText(passageKey))
            .thenThrow(ValidationException("Passage key cannot be blank"))

        // When & Then
        mockMvc.perform(get("/read/$passageKey"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Passage key cannot be blank"))
    }

    @Test
    fun `should return 502 when external API fails`() {
        // Given
        val passageKey = "Romans1"
        `when`(readService.retrievePassageText(passageKey))
            .thenThrow(ExternalServiceException("Bible API", "Failed to fetch passage text"))

        // When & Then
        mockMvc.perform(get("/read/$passageKey"))
            .andExpect(status().isBadGateway)
            .andExpect(jsonPath("$.status").value(502))
            .andExpect(jsonPath("$.error").value("Service Unavailable"))
    }

    @Test
    fun `should handle service errors properly`() {
        // Given
        val passageKey = "Invalid"
        `when`(readService.retrievePassageText(passageKey))
            .thenThrow(RuntimeException("Unexpected error"))

        // When & Then
        mockMvc.perform(get("/read/$passageKey"))
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
    }
}