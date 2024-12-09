package game.bible.passage

import game.bible.common.model.BaseEntity
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
data class Passage (
    var book: String,
    var chapter: Int,
    var verseStart: Int,
    var verseEnd: Int
) : BaseEntity()