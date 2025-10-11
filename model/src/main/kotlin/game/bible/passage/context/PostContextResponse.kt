package game.bible.passage.context

/**
 * Post-context response wrapper
 * @since 11th October 2025
 */
data class PostContextResponse(
    val passageKey: String,
    val text: String
)