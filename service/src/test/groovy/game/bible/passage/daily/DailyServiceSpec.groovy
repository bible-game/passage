package game.bible.passage.daily

import game.bible.passage.PassageRepository
import game.bible.passage.generation.GenerationService
import spock.lang.Specification
import spock.lang.Subject

import java.text.SimpleDateFormat

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

    def "should return an empty calendar when there are no passages"() {
        given:
        1 * passageRepo.findAllIdsAndDates() >> []

        when:
        def result = service.retrieveCalendar()

        then:
        result == [:]
    }

}
