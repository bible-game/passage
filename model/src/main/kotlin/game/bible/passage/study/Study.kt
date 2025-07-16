package game.bible.passage.study

import com.fasterxml.jackson.annotation.JsonManagedReference
import game.bible.common.model.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.EAGER
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

/**
 * Study Model
 * @since 10th July 2025
 */
@Entity
@Table(name = "study")
data class Study(

    val passageKey: String = "",

    @OneToMany(mappedBy = "study", cascade = [CascadeType.ALL], fetch = EAGER)
    @JsonManagedReference("study_questions")
    var questions: List<Question> = emptyList(),

    @Column(columnDefinition="TEXT")
    var goldenSummary: String = ""

) : BaseEntity()