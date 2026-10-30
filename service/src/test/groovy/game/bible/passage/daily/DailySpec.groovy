package game.bible.passage.daily

import game.bible.passage.Passage
import game.bible.passage.PassageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
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

    def "should select the book and chapter of the passages between two dates, the end excluded"() {
        given:
        passageRepo.save(new Passage(format.parse("2026-10-22"), "Ruth", "1", "", "", 22, "", "In the days"))
        passageRepo.save(new Passage(format.parse("2026-10-23"), "Genesis", "1", "", "", 31, "", "In the beginning"))
        passageRepo.save(new Passage(format.parse("2026-10-29"), "John", "3", "", "", 36, "", "For God so loved"))
        passageRepo.save(new Passage(format.parse("2026-10-30"), "Jude", "1", "", "", 25, "", "Jude, a servant"))

        when:
        def rows = passageRepo.findBooksAndChaptersBetween(format.parse("2026-10-23"), format.parse("2026-10-30"))

        then:
        rows.collect { [it[0], it[1]] } as Set == [["Genesis", "1"], ["John", "3"]] as Set
    }

    def "should hold only one passage per day"() {
        given:
        passageRepo.saveAndFlush(new Passage(format.parse("2026-10-30"), "Jude", "1", "", "", 25, "", "Jude, a servant"))

        when:
        passageRepo.saveAndFlush(new Passage(format.parse("2026-10-30"), "John", "3", "", "", 36, "", "For God so loved"))

        then:
        thrown(DataIntegrityViolationException)
    }

}
