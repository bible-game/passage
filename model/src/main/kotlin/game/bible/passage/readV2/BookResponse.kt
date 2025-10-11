package game.bible.passage.readV2

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Book response from bolls.life API
 * @since 11th October 2025
 */
data class BookResponse(
    @JsonProperty("bookid")
    val bookId: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("chapters")
    val chapters: Int,

    @JsonProperty("testament")
    val testament: String? = null
)