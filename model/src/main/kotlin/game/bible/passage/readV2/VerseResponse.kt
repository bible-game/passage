package game.bible.passage.readV2

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Verse response from bolls.life API
 * @since 11th October 2025
 */
data class VerseResponse(
    @JsonProperty("pk")
    val pk: Int,

    @JsonProperty("verse")
    val verse: Int,

    @JsonProperty("text")
    val text: String
)