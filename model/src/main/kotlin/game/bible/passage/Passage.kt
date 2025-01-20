package game.bible.passage

import game.bible.common.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

/**
 * Passage Model
 *
 * @author J. R. Smith
 * @since 7th December 2024
 */
@Entity
@Table(name = "passage")
data class Passage(
    val book: String = "",
    val chapter: Int = 0,
    val summary: String = "",
    val verseStart: Int = 0,
    val verseEnd: Int = 0,

    @Column(columnDefinition="TEXT")
    val text: String = ""
) : BaseEntity()
