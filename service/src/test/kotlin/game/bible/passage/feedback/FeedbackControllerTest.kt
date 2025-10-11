package game.bible.passage.feedback

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Unit tests for FeedbackController
 * @since 11th October 2025
 */
@WebMvcTest(controllers = [FeedbackController::class])
@AutoConfigureMockMvc(addFilters = false)
class FeedbackControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var feedbackService: FeedbackService

    @Test
    fun `should submit positive feedback successfully`() {
        // Given
        val request = FeedbackRequest(
            passageKey = "Acts8",
            feedback = FeedbackSentiment.POSITIVE,
            context = ContextType.BEFORE,
            comment = "Great context!"
        )

        val expectedResponse = FeedbackResponse(
            success = true,
            message = "Thank you for your feedback!"
        )

        `when`(feedbackService.getFeedback(request)).thenReturn(expectedResponse)

        // When & Then
        mockMvc.perform(
            post("/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Thank you for your feedback!"))
    }

    @Test
    fun `should submit negative feedback with comment`() {
        // Given
        val request = FeedbackRequest(
            passageKey = "Genesis1",
            feedback = FeedbackSentiment.NEGATIVE,
            context = ContextType.AFTER,
            comment = "Context was too brief and didn't provide enough background"
        )

        val expectedResponse = FeedbackResponse(
            success = true,
            message = "Thank you for your feedback!"
        )

        `when`(feedbackService.getFeedback(request)).thenReturn(expectedResponse)

        // When & Then
        mockMvc.perform(
            post("/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Thank you for your feedback!"))
    }

    @Test
    fun `should submit feedback without optional comment`() {
        // Given
        val request = FeedbackRequest(
            passageKey = "John3",
            feedback = FeedbackSentiment.POSITIVE,
            context = ContextType.BEFORE,
            comment = null
        )

        val expectedResponse = FeedbackResponse(
            success = true,
            message = "Thank you for your feedback!"
        )

        `when`(feedbackService.getFeedback(request)).thenReturn(expectedResponse)

        // When & Then
        mockMvc.perform(
            post("/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
    }

    @Test
    fun `should return 400 for invalid feedback sentiment`() {
        // Given
        val invalidJson = """
            {
                "passageKey": "Acts8",
                "feedback": "INVALID_SENTIMENT",
                "context": "BEFORE",
                "comment": "Test comment"
            }
        """

        // When & Then
        mockMvc.perform(
            post("/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 for invalid context type`() {
        // Given
        val invalidJson = """
            {
                "passageKey": "Acts8",
                "feedback": "POSITIVE",
                "context": "INVALID_CONTEXT",
                "comment": "Test comment"
            }
        """

        // When & Then
        mockMvc.perform(
            post("/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 for missing required fields`() {
        // Given
        val invalidJson = """
            {
                "passageKey": "Acts8"
            }
        """

        // When & Then
        mockMvc.perform(
            post("/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest)
    }
}