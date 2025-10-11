package game.bible.passage.config

import game.bible.config.model.domain.BibleConfig
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
 * Unit tests for ConfigController
 * @since 11th October 2025
 */
@WebMvcTest(controllers = [ConfigController::class])
@AutoConfigureMockMvc(addFilters = false)
class ConfigControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var bibleConfig: BibleConfig

    @Test
    fun `should return bible config successfully`() {
        // Given
        `when`(bibleConfig.getTestaments()).thenReturn(emptyList())

        // When & Then
        mockMvc.perform(get("/config/bible"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.config").exists())
    }
}