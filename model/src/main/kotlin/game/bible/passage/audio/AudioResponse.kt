package game.bible.passage.audio

/**
 * Audio response wrapper
 * @since 11th October 2025
 */
data class AudioResponse(
    val passageKey: String,
    val audioData: ByteArray,
    val contentType: String = "audio/mpeg",
    val sizeBytes: Int = audioData.size
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioResponse

        if (passageKey != other.passageKey) return false
        if (!audioData.contentEquals(other.audioData)) return false
        if (contentType != other.contentType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = passageKey.hashCode()
        result = 31 * result + audioData.contentHashCode()
        result = 31 * result + contentType.hashCode()
        return result
    }
}