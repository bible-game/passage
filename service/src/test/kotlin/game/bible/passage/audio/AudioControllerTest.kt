package game.bible.passage.audio

import game.bible.passage.exception.ValidationException
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Unit tests for AudioController
 * @since 11th October 2025
 */
@WebMvcTest(controllers = [AudioController::class])
@AutoConfigureMockMvc(addFilters = false)
class AudioControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var audioService: AudioService

    @Test
    fun `should return audio successfully`() {
        // Given
        val passageKey = "Psalm117"
        val audioData = ByteArray(1024) { it.toByte() }
        val response = AudioResponse(
            passageKey = passageKey,
            audioData = audioData
        )

        `when`(audioService.retrieveAudio(passageKey)).thenReturn(response)

        // When & Then
        mockMvc.perform(get("/audio/$passageKey"))
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "audio/mpeg"))
            .andExpect(header().string("Content-Length", "1024"))
            .andExpect(content().bytes(audioData))
    }

    @Test
    fun `should return 400 when passage key is blank`() {
        // Given
        val passageKey = "   "
        `when`(audioService.retrieveAudio(passageKey))
            .thenThrow(ValidationException("Passage key cannot be blank"))

        // When & Then
        mockMvc.perform(get("/audio/$passageKey"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Passage key cannot be blank"))
            .andExpect(jsonPath("$.path").value("/audio/%20%20%20"))
    }

    @Test
    fun `should handle service errors properly`() {
        // Given
        val passageKey = "InvalidPassage"
        `when`(audioService.retrieveAudio(passageKey))
            .thenThrow(RuntimeException("Unexpected error"))

        // When & Then
        mockMvc.perform(get("/audio/$passageKey"))
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
    }
}