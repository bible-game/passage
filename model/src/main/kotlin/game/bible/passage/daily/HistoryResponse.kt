package game.bible.passage.daily

/**
 * History response wrapper with paginated dates
 * @since 11th October 2025
 */
data class HistoryResponse(
    val dates: List<String>,
    val page: Int,
    val total: Int
)