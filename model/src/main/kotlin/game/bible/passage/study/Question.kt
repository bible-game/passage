package game.bible.passage.study

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import game.bible.common.model.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

/**
 * Study Question Model
 * @since 10th July 2025
 */
@Entity
@Table(name = "question")
class Question @JsonCreator constructor(
    @JsonProperty("content") val content:         String,
    @JsonProperty("optionOne") val optionOne:     String,
    @JsonProperty("optionTwo") val optionTwo:     String,
    @JsonProperty("optionThree") val optionThree: String,
    @JsonProperty("correct") val correct:         String
) : BaseEntity() {

    @ManyToOne
    @JoinColumn(name = "study_id", referencedColumnName = "id")
    @JsonBackReference("study_questions")
    var study: Study? = null

    constructor() : this("", "", "", "", "")
}