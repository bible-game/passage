package game.bible.passage.daily

import game.bible.passage.Passage
import game.bible.passage.PassageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.text.SimpleDateFormat

/**
 * Data tests for the daily passage queries, run against H2
 * @since 27th October 2026
 */
@DataJpaTest
@ActiveProfiles("test")
class DailySpec extends Specification {

    @Autowired
    PassageRepository passageRepo

    def format = new SimpleDateFormat("yyyy-MM-dd")

    def "should select the id and date of every passage"() {
        given:
        def first = passageRepo.save(new Passage(format.parse("2026-10-26"), "Genesis", "1", "", "", 31, "", "In the beginning"))
        def second = passageRepo.save(new Passage(format.parse("2026-10-27"), "John", "3", "", "", 36, "", "For God so loved"))

        when:
        def rows = passageRepo.findAllIdsAndDates()

        then:
        rows.collect { [it[0], format.format(it[1])] } as Set == [[first.id, "2026-10-26"], [second.id, "2026-10-27"]] as Set
    }

}
