package game.bible.passage.daily

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
 * Unit tests for DailyController
 * @since 11th October 2025
 */
@WebMvcTest(controllers = [DailyController::class])
@AutoConfigureMockMvc(addFilters = false)
class DailyControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var dailyService: DailyService

    @Test
    fun `should return daily passage successfully`() {
        // Given
        val date = Date()
        val response = DailyPassageResponse(
            date = date,
            book = "Genesis",
            chapter = "1",
            title = "The Beginning",
            summary = "In the beginning God created...",
            verses = 31,
            icon = "icon_genesis_1",
            text = "In the beginning God created the heavens and the earth..."
        )

        `when`(dailyService.retrievePassage(date)).thenReturn(response)

        // When & Then
        mockMvc.perform(get("/daily/2025-07-04"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.book").value("Genesis"))
            .andExpect(jsonPath("$.chapter").value("1"))
            .andExpect(jsonPath("$.summary").value("In the beginning God created..."))
            .andExpect(jsonPath("$.verses").value(31))
    }

    @Test
    fun `should return history successfully`() {
        // Given
        val response = HistoryResponse(
            dates = listOf("2025-07-01", "2025-07-02", "2025-07-03"),
            page = 0,
            total = 3
        )

        `when`(dailyService.retrieveDates(0)).thenReturn(response)

        // When & Then
        mockMvc.perform(get("/daily/history"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.dates").isArray)
            .andExpect(jsonPath("$.dates.length()").value(3))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.total").value(3))
    }

    @Test
    fun `should return history with page parameter`() {
        // Given
        val response = HistoryResponse(
            dates = listOf("2025-06-01", "2025-06-02"),
            page = 1,
            total = 2
        )

        `when`(dailyService.retrieveDates(1)).thenReturn(response)

        // When & Then
        mockMvc.perform(get("/daily/history?page=1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.total").value(2))
    }

    @Test
    fun `should return 400 when page is negative`() {
        // Given
        `when`(dailyService.retrieveDates(-1))
            .thenThrow(ValidationException("Page number cannot be negative"))

        // When & Then
        mockMvc.perform(get("/daily/history?page=-1"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Page number cannot be negative"))
    }

    @Test
    fun `should handle service errors properly`() {
        // Given
        val date = Date()
        `when`(dailyService.retrievePassage(date))
            .thenThrow(RuntimeException("Unexpected error"))

        // When & Then
        mockMvc.perform(get("/daily/2025-07-04"))
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
    }
}