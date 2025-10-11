package game.bible.passage.readV2

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc


/** * Unit tests for ReadControllerV2
 * @since 11th October 2025
 */
@WebMvcTest(controllers = [ReadControllerV2::class])
@AutoConfigureMockMvc(addFilters = false)
class ReadV2ControllerTest {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @MockitoBean
  private lateinit var readServiceV2: ReadServiceV2

  @Test
  fun `should return Bible versions successfully`() {
  }
  @Test
  fun `should return books for a specific version`() {}

  @Test
  fun `should return a specific book by name from a version`() {}

  @Test
  fun `should return Chapter verses using book ID`() {}

  @Test
  fun `should return Chapter verses using book name`() {}

  @Test
  fun `should return Verse text using book ID`() {}

  @Test
  fun `should clear cache successfully`() {}
}