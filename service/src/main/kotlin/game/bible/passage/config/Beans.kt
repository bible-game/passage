package game.bible.passage.config

import game.bible.config.ReloadableConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Service Configuration
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Configuration
@Import(ReloadableConfig::class)
class Beans