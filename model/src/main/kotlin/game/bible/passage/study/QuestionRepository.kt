package game.bible.passage.study

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Question Data Access
 * @since 10th July 2025
 */
@Repository
interface QuestionRepository : JpaRepository<Question, Long>