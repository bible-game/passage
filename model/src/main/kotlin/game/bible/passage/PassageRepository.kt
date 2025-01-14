package game.bible.passage

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Passage Data Access
 *
 * @author J. R. Smith
 * @since 9th December 2024
 */
@Repository
interface PassageRepository : JpaRepository<Passage, Long> {

    @Query(
        "SELECT p FROM Passage p " +
        "WHERE p.createdDate >= current_date " +
        "ORDER BY p.createdDate ASC LIMIT 1")
    fun findToday(): Optional<Passage>

}