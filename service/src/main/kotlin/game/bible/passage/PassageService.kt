package game.bible.passage

import game.bible.config.ReloadableConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(ReloadableConfig::class)
class PassageService

fun main(args: Array<String>) {
	runApplication<PassageService>(*args)
}
