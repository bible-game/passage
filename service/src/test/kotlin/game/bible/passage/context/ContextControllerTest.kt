package game.bible.passage.context

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
 * Unit tests for ContextController
 * @since 11th October 2025
 */
@WebMvcTest(controllers = [ContextController::class])
@AutoConfigureMockMvc(addFilters = false)
class ContextControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var contextService: ContextService

    @Test
    fun `should return pre-context successfully`() {
        // Given
        val passageKey = "Judges4"
        val response = PreContextResponse(
            passageKey = passageKey,
            text = "This is the context before the passage..."
        )

        `when`(contextService.retrievePreContext(passageKey)).thenReturn(response)

        // When & Then
        mockMvc.perform(get("/context/before/$passageKey"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.passageKey").value(passageKey))
            .andExpect(jsonPath("$.text").value("This is the context before the passage..."))
    }

    @Test
    fun `should return post-context successfully`() {
        // Given
        val passageKey = "Judges4"
        val response = PostContextResponse(
            passageKey = passageKey,
            text = "This is the context after the passage..."
        )

        `when`(contextService.retrievePostContext(passageKey)).thenReturn(response)

        // When & Then
        mockMvc.perform(get("/context/after/$passageKey"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.passageKey").value(passageKey))
            .andExpect(jsonPath("$.text").value("This is the context after the passage..."))
    }

    @Test
    fun `should return 400 when passage key is blank for pre-context`() {
        // Given
        val passageKey = "   "
        `when`(contextService.retrievePreContext(passageKey))
            .thenThrow(ValidationException("Passage key cannot be blank"))

        // When & Then
        mockMvc.perform(get("/context/before/$passageKey"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Passage key cannot be blank"))
    }

    @Test
    fun `should return 400 when passage key is blank for post-context`() {
        // Given
        val passageKey = "   "
        `when`(contextService.retrievePostContext(passageKey))
            .thenThrow(ValidationException("Passage key cannot be blank"))

        // When & Then
        mockMvc.perform(get("/context/after/$passageKey"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Passage key cannot be blank"))
    }

    @Test
    fun `should handle service errors properly`() {
        // Given
        val passageKey = "InvalidPassage"
        `when`(contextService.retrievePreContext(passageKey))
            .thenThrow(RuntimeException("Unexpected error"))

        // When & Then
        mockMvc.perform(get("/context/before/$passageKey"))
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
    }
}