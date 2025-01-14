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
class Passage(
    val book: String,
    val chapter: Int,
    val verseStart: Int,
    val verseEnd: Int,

    @Column(columnDefinition="TEXT")
    val text: String
) : BaseEntity()