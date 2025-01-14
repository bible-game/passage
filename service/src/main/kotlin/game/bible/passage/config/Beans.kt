package game.bible.passage.config

import game.bible.config.ReloadableConfig
import game.bible.config.model.service.PassageConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Bean Configuration
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Configuration
@Import(ReloadableConfig::class, PassageConfig::class)
class Beans