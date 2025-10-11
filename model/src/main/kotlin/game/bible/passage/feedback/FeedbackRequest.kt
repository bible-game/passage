package game.bible.passage.feedback

/**
 * Feedback request data
 * @since 11th October 2025
 */
data class FeedbackRequest(
    val passageKey: String,
    val feedback: FeedbackSentiment,
    val context: ContextType,
    val comment: String? = null
)

/**
 * Feedback sentiment enum
 */
enum class FeedbackSentiment {
    POSITIVE,
    NEGATIVE
}

/**
 * Context type enum
 */
enum class ContextType {
    BEFORE,
    AFTER
}
