package game.bible.passage

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Passage Data Access
 *
 * @author J. R. Smith
 * @since 9th December 2024
 */
@Repository
interface PassageRepository : JpaRepository<Passage, Long>