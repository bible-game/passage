package game.bible.passage.config

import game.bible.config.ReloadableConfig
import game.bible.config.model.integration.BibleApiConfiguration
import game.bible.config.model.service.PassageConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.client.RestClient

/**
 * Bean Configuration
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Configuration
@Import(
    ReloadableConfig::class,
    PassageConfig::class,
    BibleApiConfiguration::class)
class Beans {

    @Bean
    fun restClient(): RestClient {
        return RestClient.create()
    }

}