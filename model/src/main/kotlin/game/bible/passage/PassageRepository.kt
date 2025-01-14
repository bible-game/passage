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
        value = "SELECT TOP 1 * FROM Passage " +
                "WHERE created_date >= CURDATE()", nativeQuery = true)
    // Improvement :: could place upper bound using tomorrow's date
    fun findToday(): Optional<Passage>

}