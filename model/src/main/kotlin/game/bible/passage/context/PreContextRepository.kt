package game.bible.passage.context

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Pre-Context Data Access
 *
 * @since 3rd July 2025
 */
@Repository
interface PreContextRepository : JpaRepository<PreContext, Long> {

    fun findByPassageKey(key: String): Optional<PreContext>

    fun deleteByPassageKey(key: String): Int
}
