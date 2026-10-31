package game.bible.passage

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
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

    @Query("SELECT p.id, p.date FROM Passage p")
    fun findAllIdsAndDates(): List<Array<Any>>

    @Query("SELECT p.book, p.chapter FROM Passage p WHERE p.date >= :from AND p.date < :until")
    fun findBooksAndChaptersBetween(from: Date, until: Date): List<Array<Any>>

}