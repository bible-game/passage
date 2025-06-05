package game.bible.passage

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Date
import java.util.Optional

/**
 * Passage Data Access
 * @since 9th December 2024
 */
@Repository
interface PassageRepository : JpaRepository<Passage, Long> {

    fun findByDate(date: Date): Optional<Passage>

}