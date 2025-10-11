package game.bible.passage.feedback

/**
 * Feedback request data
 * @since 11th October 2025
 */
data class FeedbackRequest(
    val passageKey: String,
    val feedback: FeedbackSentiment,
    val promptType: PromptType,
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
enum class PromptType {
    PRE_CONTEXT,
    POST_CONTEXT,
    DAILY,
    STUDY,
    GOLDEN,
    FEEDBACK

}
