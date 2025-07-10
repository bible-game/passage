package game.bible.passage.context

import game.bible.common.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

/**
 * Pre-Context Model
 * @since 3rd July 2025
 */
@Entity
@Table(name = "pre_context")
data class PreContext(
    val passageKey: String = "",

    @Column(columnDefinition="TEXT")
    val text: String = ""
) : BaseEntity()
