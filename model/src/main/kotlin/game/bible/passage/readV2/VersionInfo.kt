package game.bible.passage.readV2

/**
 * Bible version information
 * @since 11th October 2025
 */
data class VersionInfo(
    val code: String,
    val name: String? = null,
    val language: String? = null
) {
    companion object {
        // English Bible Versions as referenced in the React Native code
        val ENGLISH_VERSIONS = listOf(
            "YLT", "KJV", "NKJV", "WEB", "RSV", "CJB", "TS2009", "LXXE",
            "TLV", "NASB", "ESV", "GNV", "DRB", "NIV2011", "NIV", "NLT",
            "NRSVCE", "NET", "NJB1985", "AMP", "MSG", "LSV"
        )

        fun getEnglishVersions(): List<VersionInfo> {
            return ENGLISH_VERSIONS.map { VersionInfo(code = it) }
        }
    }
}