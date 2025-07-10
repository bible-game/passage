package game.bible.passage.study

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Study Data Access
 * @since 10th July 2025
 */
@Repository
interface StudyRepository : JpaRepository<Study, Long> {

    fun findByPassageKey(key: String): Optional<Study>

}