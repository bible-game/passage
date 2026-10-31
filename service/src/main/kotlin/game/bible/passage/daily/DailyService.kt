package game.bible.passage.daily

import game.bible.passage.Passage
import game.bible.passage.PassageRepository
import game.bible.passage.generation.GenerationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date
import java.util.concurrent.ConcurrentHashMap

private val log = KotlinLogging.logger {}

/**
 * Daily Passage Service Logic
 * @since 7th December 2024
 */
@Service
class DailyService(
    private val generator: GenerationService,
    private val passageRepository: PassageRepository) {

    private var date = SimpleDateFormat("yyyy-MM-dd")

    private val generating = ConcurrentHashMap<Date, Any>()

    /** Generates a bible passage and retrieves it from storage */
    fun retrievePassage(date: Date): Passage {
        val entry = passageRepository.findByDate(date)

        return if (entry.isPresent) {
            entry.get()

        } else generatePassage(date)
    }

    /** Retrieves paginated list of historic daily passage */
    fun retrieveDates(page: Int): List<String> {
        val passages = passageRepository.findAll()
        // TODO :: for a logged in user, return paginated results of all dates (attempted [color?], won [star?], not-attempted [question mark?])
        // for non-logged in user, just return all existing dates (paginate as required)
        // pagination -> pull back a month at a time!

        return passages.map { date.format(it.date) }
    }

    /** Maps each historic daily passage's date to its passage id */
    fun retrieveCalendar(): Map<String, Long> =
        passageRepository.findAllIdsAndDates().associate { (id, day) -> date.format(day as Date) to id as Long }

    companion object {
        private val EARLIEST_ZONE = ZoneOffset.ofHours(14)

        const val REST_DAYS = 7L

        @JvmStatic
        fun isReleased(date: Date, now: Instant): Boolean {
            val day = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            return !day.isAfter(now.atZone(EARLIEST_ZONE).toLocalDate())
        }
    }

    private fun generatePassage(date: Date): Passage {
        val lock = generating.computeIfAbsent(date) { Any() }
        try {
            synchronized(lock) {
                // Another request may have made it while this one waited
                passageRepository.findByDate(date).orElse(null)?.let { return it }

                log.info { "No entry exists for [$date]! Generating random passage" }
                // The chapters of the week before, which can't come up again yet; only those days are read, so this
                // stays quick. Calendar days, so a change of the clocks doesn't shift the window by an hour
                val weekBefore = Date.from(date.toInstant().atZone(ZoneId.systemDefault()).minusDays(REST_DAYS).toInstant())
                val recent = passageRepository.findBooksAndChaptersBetween(weekBefore, date)
                    .map { (book, chapter) -> GenerationService.key(book as String, chapter as String) }
                    .toSet()
                val randomPassage = generator.random(date, recent)

                return try {
                    passageRepository.saveAndFlush(randomPassage)
                } catch (e: DataIntegrityViolationException) {
                    // Another instance saved this day's passage first: there is one passage per day, so theirs stands
                    log.info { "Passage for [$date] was made elsewhere first; returning that one" }
                    passageRepository.findByDate(date).orElseThrow { e }
                }
            }
        } finally {
            generating.remove(date, lock)
        }
    }

}