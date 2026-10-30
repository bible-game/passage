package game.bible.passage.generation

import com.fasterxml.jackson.databind.ObjectMapper
import game.bible.config.model.domain.BibleConfig.BibleTestamentConfig.BibleDivisionConfig.BibleBookConfig
import spock.lang.Specification

/**
 * Unit tests for how {@link GenerationService} picks the daily chapter
 * @since 30th October 2026
 */
class GenerationServiceSpec extends Specification {

    def mapper = new ObjectMapper()

    def book(String name, int chapters) {
        mapper.readValue("""{"name": "$name", "chapters": $chapters}""", BibleBookConfig)
    }

    def books = [book("Jude", 1), book("John", 3)]

    def picks(int times, Set<String> recent) {
        (1..times).collect { GenerationService.pick(books, recent) }.collect { "${it.first.name}:${it.second}" }
    }

    def "should key a chapter by book name and chapter"() {
        expect:
        GenerationService.key("John", "3") == "John:3"
    }

    def "should give every chapter the same chance, not every book"() {
        when:
        def counts = picks(400, [] as Set).countBy { it }

        then: "Jude's one chapter is as likely as each of John's three, rather than Jude coming up half the time"
        counts.keySet() == ["Jude:1", "John:1", "John:2", "John:3"] as Set
        counts.values().every { it > 50 && it < 150 }
    }

    def "should not pick a recent chapter"() {
        when:
        def picked = picks(200, ["Jude:1", "John:1"] as Set) as Set

        then:
        picked == ["John:2", "John:3"] as Set
    }

    def "should still pick something if every chapter were recent"() {
        when:
        def picked = picks(50, ["Jude:1", "John:1", "John:2", "John:3"] as Set) as Set

        then:
        !picked.isEmpty()
    }

}
