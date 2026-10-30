package game.bible.passage.daily

import game.bible.passage.Passage
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

import java.text.SimpleDateFormat

/**
 * Unit tests for {@link DailyController}
 * @since 30th October 2026
 */
class DailyControllerSpec extends Specification {

    def service = Mock(DailyService)

    @Subject
    def controller = new DailyController(service)

    def format = new SimpleDateFormat("yyyy-MM-dd")

    def "should refuse a day that hasn't begun anywhere, without dealing its passage"() {
        when:
        def response = controller.getPassage(format.parse("2999-01-01"))

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        0 * service.retrievePassage(_)
    }

    def "should return a past day's passage"() {
        given:
        def date = format.parse("2026-10-26")
        def passage = new Passage(date, "Genesis", "1", "", "", 31, "", "In the beginning")

        when:
        def response = controller.getPassage(date)

        then:
        1 * service.retrievePassage(date) >> passage
        response.statusCode == HttpStatus.OK
        response.body == passage
    }

}
