package game.bible.passage.daily

import java.util.Date

/**
 * Daily passage response wrapper
 * @since 11th October 2025
 */
data class DailyPassageResponse(
    val date: Date,
    val book: String,
    val chapter: String,
    val title: String,
    val summary: String,
    val verses: Int,
    val icon: String,
    val text: String
)