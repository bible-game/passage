package game.bible.passage.context

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Post-Context Data Access
 *
 * @since 3rd July 2025
 */
@Repository
interface PostContextRepository : JpaRepository<PostContext, Long> {

    fun findByPassageKey(key: String): Optional<PostContext>

}