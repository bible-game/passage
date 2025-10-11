# Read Service V2 - Bolls.Life API Integration

## Overview
ReadServiceV2 is a comprehensive Bible reading service that integrates with the [Bolls.Life API](https://bolls.life/api/) to provide access to multiple Bible versions, books, chapters, and verses.

## Features
- **Multiple Bible Versions**: Support for 22 English Bible translations (NIV, KJV, ESV, NLT, etc.)
- **Full Book Access**: Retrieve complete book lists for any supported version
- **Chapter and Verse Access**: Get individual verses, verse ranges, or complete chapters
- **Intelligent Caching**: Built-in caching for books and verses to improve performance
- **RESTful API**: Clean, intuitive REST endpoints for all operations

## Architecture

### Components

#### Data Models
Located in `passage/model/src/main/kotlin/game/bible/passage/readV2/`

- **VersionInfo**: Bible version information with supported English versions
- **BookResponse**: Book metadata including bookId, name, chapters, and testament
- **VerseResponse**: Individual verse with pk, verse number, and text
- **ReadResponseV2**: Complete chapter response with version, book, and verses

#### Service Layer
`ReadServiceV2.kt` - Core business logic including:
- Version management
- Book retrieval and caching
- Chapter and verse fetching
- Cache management

#### Configuration
`BollsLifeApiConfig.kt` - API configuration with base URL and endpoint builders

#### Controller
`ReadControllerV2.kt` - REST API endpoints

## API Endpoints

### 1. Get Available Versions
```
GET /v2/read/versions
```
Returns list of all supported Bible versions.

**Response:**
```json
[
  {
    "code": "NIV",
    "name": null,
    "language": null
  },
  {
    "code": "KJV",
    "name": null,
    "language": null
  }
]
```

### 2. Get Books for Version
```
GET /v2/read/books/{version}
```
Get all books available in a specific Bible version.

**Example:** `GET /v2/read/books/NIV`

**Response:**
```json
[
  {
    "bookId": "01O",
    "name": "Genesis",
    "chapters": 50,
    "testament": "OT"
  },
  {
    "bookId": "02O",
    "name": "Exodus",
    "chapters": 40,
    "testament": "OT"
  }
]
```

### 3. Get Specific Book
```
GET /v2/read/book/{version}/{bookName}
```
Get metadata for a specific book.

**Example:** `GET /v2/read/book/NIV/Genesis`

**Response:**
```json
{
  "bookId": "01O",
  "name": "Genesis",
  "chapters": 50,
  "testament": "OT"
}
```

### 4. Get Chapter by Book ID
```
GET /v2/read/chapter/{version}/{bookId}/{chapter}
```
Get all verses in a chapter using the book ID.

**Example:** `GET /v2/read/chapter/NIV/01O/1`

**Response:**
```json
{
  "version": "NIV",
  "bookId": "01O",
  "bookName": "Genesis",
  "chapter": 1,
  "verses": [
    {
      "pk": 1,
      "verse": 1,
      "text": "In the beginning God created the heavens and the earth."
    },
    {
      "pk": 2,
      "verse": 2,
      "text": "Now the earth was formless and empty..."
    }
  ],
  "cached": false
}
```

### 5. Get Chapter by Book Name
```
GET /v2/read/chapter-by-name/{version}/{bookName}/{chapter}
```
Get all verses in a chapter using the book name.

**Example:** `GET /v2/read/chapter-by-name/NIV/Genesis/1`

### 6. Get Specific Verse
```
GET /v2/read/verse/{version}/{bookId}/{chapter}/{verse}
```
Get a single verse.

**Example:** `GET /v2/read/verse/NIV/01O/1/1`

**Response:**
```json
{
  "pk": 1,
  "verse": 1,
  "text": "In the beginning God created the heavens and the earth."
}
```

### 7. Get Verse Range
```
GET /v2/read/verse-range/{version}/{bookId}/{chapter}?start={start}&end={end}
```
Get a range of verses from a chapter.

**Example:** `GET /v2/read/verse-range/NIV/01O/1?start=1&end=5`

**Response:**
```json
[
  {
    "pk": 1,
    "verse": 1,
    "text": "In the beginning God created the heavens and the earth."
  },
  {
    "pk": 2,
    "verse": 2,
    "text": "Now the earth was formless and empty..."
  }
]
```

### 8. Clear All Caches
```
DELETE /v2/read/cache
```
Clear all cached data.

**Response:**
```json
{
  "message": "All caches cleared successfully"
}
```

### 9. Clear Version Cache
```
DELETE /v2/read/cache/{version}
```
Clear cached data for a specific version.

**Example:** `DELETE /v2/read/cache/NIV`

**Response:**
```json
{
  "message": "Cache cleared for version: NIV"
}
```

## Supported Bible Versions

The following English Bible versions are supported:
- YLT (Young's Literal Translation)
- KJV (King James Version)
- NKJV (New King James Version)
- WEB (World English Bible)
- RSV (Revised Standard Version)
- CJB (Complete Jewish Bible)
- TS2009 (The Scriptures 2009)
- LXXE (Septuagint)
- TLV (Tree of Life Version)
- NASB (New American Standard Bible)
- ESV (English Standard Version)
- GNV (Geneva Bible)
- DRB (Douay-Rheims Bible)
- NIV2011 (New International Version 2011)
- NIV (New International Version)
- NLT (New Living Translation)
- NRSVCE (New Revised Standard Version Catholic Edition)
- NET (New English Translation)
- NJB1985 (New Jerusalem Bible)
- AMP (Amplified Bible)
- MSG (The Message)
- LSV (Literal Standard Version)

## Configuration

Add the following to your `application.properties` or `application.yml`:

```properties
# Optional: Override default base URL
bolls.api.base-url=https://bolls.life
```

## Caching Strategy

The service implements two-level caching:

1. **Books Cache**: Caches the book list for each version (ConcurrentHashMap)
2. **Verses Cache**: Caches chapter verses using the key format: `{version}:{bookId}:{chapter}`

Cache benefits:
- Reduces API calls to Bolls.Life
- Improves response times for repeated requests
- Thread-safe with ConcurrentHashMap

## Error Handling

The service throws:
- `ValidationException`: For invalid input parameters
- `ExternalServiceException`: When Bolls.Life API calls fail

## Migration from V1

### V1 (Old API)
```kotlin
// V1 - Single passage text
GET /read/{key}
// Example: /read/genesis1
```

### V2 (New API)
```kotlin
// V2 - Structured, versioned access
GET /v2/read/chapter-by-name/{version}/{bookName}/{chapter}
// Example: /v2/read/chapter-by-name/NIV/Genesis/1
```

### Key Differences
1. **Multiple Versions**: V2 supports 22 Bible versions, V1 had a single version
2. **Structured Data**: V2 returns JSON with verse-by-verse breakdown
3. **Flexible Access**: V2 offers multiple ways to access the same content
4. **Better Caching**: V2 has more granular cache control
5. **RESTful Design**: V2 follows REST conventions more closely

## Usage Examples

### Kotlin/Spring
```kotlin
@Autowired
private lateinit var readServiceV2: ReadServiceV2

fun example() {
    // Get all NIV books
    val books = readServiceV2.getBooksForVersion("NIV")

    // Get Genesis 1 in ESV
    val chapter = readServiceV2.getChapterTextByBookName("ESV", "Genesis", 1)

    // Get a specific verse
    val verse = readServiceV2.getSpecificVerse("KJV", "01O", 1, 1)

    // Get verse range
    val verses = readServiceV2.getVerseRange("NIV", "01O", 1, 1, 5)
}
```

### cURL Examples
```bash
# Get all versions
curl http://localhost:8080/v2/read/versions

# Get NIV books
curl http://localhost:8080/v2/read/books/NIV

# Get Genesis 1 in NIV
curl http://localhost:8080/v2/read/chapter-by-name/NIV/Genesis/1

# Get John 3:16 in KJV (assuming John's bookId is 43N)
curl http://localhost:8080/v2/read/verse/KJV/43N/3/16

# Get verse range
curl "http://localhost:8080/v2/read/verse-range/ESV/01O/1?start=1&end=5"

# Clear cache
curl -X DELETE http://localhost:8080/v2/read/cache
```

## Performance Considerations

1. **First Request**: Slower as data is fetched from Bolls.Life API
2. **Subsequent Requests**: Faster due to caching
3. **Memory Usage**: Cache grows with usage; use cache clearing endpoints if needed
4. **Thread Safety**: All operations are thread-safe

## Future Enhancements

Potential improvements:
- Add support for more languages and versions
- Implement Redis or distributed caching
- Add search functionality across verses
- Support for parallel passages
- Add rate limiting for API protection
- Implement pagination for large book lists

## Related Resources

- [Bolls.Life API Documentation](https://bolls.life/api/)
- Original React Native implementation (reference provided in task)
- ReadService V1: `passage/service/.../read/ReadService.kt`

---

**Created:** 11th October 2025
**Last Updated:** 11th October 2025
**Version:** 2.0.0