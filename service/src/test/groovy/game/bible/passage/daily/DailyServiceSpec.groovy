package game.bible.passage.daily

import game.bible.passage.Passage
import game.bible.passage.PassageRepository
import game.bible.passage.generation.GenerationService
import org.springframework.dao.DataIntegrityViolationException
import spock.lang.Specification
import spock.lang.Subject

import java.text.SimpleDateFormat
import java.time.Instant

/**
 * Unit tests for {@link DailyService}
 * @since 27th October 2026
 */
class DailyServiceSpec extends Specification {

    def generator = Mock(GenerationService)
    def passageRepo = Mock(PassageRepository)

    @Subject
    def service = new DailyService(generator, passageRepo)

    def format = new SimpleDateFormat("yyyy-MM-dd")

    def "should map each passage's date to its id, without loading whole passages"() {
        given:
        1 * passageRepo.findAllIdsAndDates() >> [
            [1L, format.parse("2026-10-26")] as Object[],
            [2L, format.parse("2026-10-27")] as Object[]
        ]

        when:
        def result = service.retrieveCalendar()

        then:
        result == ["2026-10-26": 1L, "2026-10-27": 2L]
        0 * passageRepo.findAll()
    }

    def "should pick a new day's passage, leaving out the week before's chapters"() {
        given:
        def date = format.parse("2026-10-30")
        def passage = new Passage(date, "Jude", "1", "", "", 25, "", "Jude, a servant")
        passageRepo.findByDate(date) >> Optional.empty()

        when:
        def result = service.retrievePassage(date)

        then: "only the seven days before are read"
        1 * passageRepo.findBooksAndChaptersBetween(format.parse("2026-10-23"), date) >> [
            ["Genesis", "1"] as Object[],
            ["John", "3"] as Object[]
        ]
        1 * generator.random(date, ["Genesis:1", "John:3"] as Set) >> passage
        1 * passageRepo.saveAndFlush(passage) >> passage
        result == passage
    }

    def "should return the passage another instance saved first, rather than make a second for the day"() {
        given:
        def date = format.parse("2026-10-30")
        def ours = new Passage(date, "Jude", "1", "", "", 25, "", "Jude, a servant")
        def theirs = new Passage(date, "John", "3", "", "", 36, "", "For God so loved")
        passageRepo.findByDate(date) >>> [Optional.empty(), Optional.empty(), Optional.of(theirs)]
        passageRepo.findBooksAndChaptersBetween(_, _) >> []
        generator.random(date, _) >> ours

        when:
        def result = service.retrievePassage(date)

        then:
        1 * passageRepo.saveAndFlush(ours) >> { throw new DataIntegrityViolationException("uq_passage_date") }
        result == theirs
    }

    def "should return a stored day's passage without picking a new one"() {
        given:
        def date = format.parse("2026-10-30")
        def passage = new Passage(date, "John", "3", "", "", 36, "", "For God so loved")
        passageRepo.findByDate(date) >> Optional.of(passage)

        when:
        def result = service.retrievePassage(date)

        then:
        result == passage
        0 * generator.random(*_)
        0 * passageRepo.findBooksAndChaptersBetween(*_)
    }

    def "should release a day once it has begun in the earliest time zone, not before"() {
        expect:
        DailyService.isReleased(format.parse(day), Instant.parse(now)) == released

        where:
        day          | now                    || released
        "2026-10-29" | "2026-10-30T12:00:00Z" || true
        "2026-10-30" | "2026-10-30T12:00:00Z" || true
        "2026-10-31" | "2026-10-30T12:00:00Z" || true  // already the 31st at UTC+14 (02:00)
        "2026-11-01" | "2026-10-30T12:00:00Z" || false
        "2026-10-31" | "2026-10-30T09:00:00Z" || false // still the 30th at UTC+14 (23:00)
    }

    def "should return an empty calendar when there are no passages"() {
        given:
        1 * passageRepo.findAllIdsAndDates() >> []

        when:
        def result = service.retrieveCalendar()

        then:
        result == [:]
    }

}
