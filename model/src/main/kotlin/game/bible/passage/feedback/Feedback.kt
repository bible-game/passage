package game.bible.passage.feedback

/**
 * User Feedback
 * @since 11th October 2025
 */
data class Feedback(
    val passageKey: String,
    val sentiment: Sentiment,
    val prompt: Prompt,
    val comment: String? = null
)
