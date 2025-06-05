package game.bible.passage

import game.bible.common.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.Date

/**
 * Passage Model
 * @since 7th December 2024
 */
@Entity
@Table(name = "passage")
data class Passage(
    val date: Date = Date(),
    val book: String = "",
    val chapter: String = "",
    val title: String = "",
    val summary: String = "",
    val verses: Int = 0,

    @Column(columnDefinition="TEXT")
    val text: String = ""
) : BaseEntity()
