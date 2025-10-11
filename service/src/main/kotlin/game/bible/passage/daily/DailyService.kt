package game.bible.passage.daily

import game.bible.passage.Passage
import game.bible.passage.PassageRepository
import game.bible.passage.exception.ValidationException
import game.bible.passage.generation.GenerationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Date


private val log = KotlinLogging.logger {}

/**
 * Daily Passage Service Logic
 *
 * @since 7th December 2024
 */
@Service
class DailyService(
    private val generator: GenerationService,
    private val passageRepository: PassageRepository) {

    private var dateFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

    /** Generates a bible passage and retrieves it from storage */
    fun retrievePassage(date: Date): DailyPassageResponse {
        val entry = passageRepository.findByDate(date)
        val passage = if (entry.isPresent) {
            entry.get()
        } else {
            generatePassage(date)
        }

        return DailyPassageResponse(
            date = passage.date,
            book = passage.book,
            chapter = passage.chapter,
            title = passage.title,
            summary = passage.summary,
            verses = passage.verses,
            icon = passage.icon,
            text = passage.text
        )
    }

    /** Retrieves paginated list of historic daily passage */
    fun retrieveDates(page: Int): HistoryResponse {
        if (page < 0) {
            throw ValidationException("Page number cannot be negative")
        }

        val passages = passageRepository.findAll()
        // TODO: for a logged in user, return paginated results of all dates (attempted [color?], won [star?], not-attempted [question mark?])
        // for non-logged in user, just return all existing dates (paginate as required)
        // pagination -> pull back a month at a time!

        val dates = passages.map { dateFormatter.format(it.date) }

        return HistoryResponse(
            dates = dates,
            page = page,
            total = dates.size
        )
    }

    private fun generatePassage(date: Date): Passage {
        log.info { "No entry exists for [$date]! Generating random passage" }
        val randomPassage = generator.random(date)

        return passageRepository.save(randomPassage)
    }

}