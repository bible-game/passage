package game.bible.passage.config

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import game.bible.common.util.resource.S3BucketService
import game.bible.config.ReloadableConfig
import game.bible.config.model.integration.AwsConfig
import game.bible.config.model.integration.BibleApiConfig
import game.bible.config.model.integration.ChatGptConfig
import game.bible.config.model.service.PassageConfig
import org.springframework.ai.openai.OpenAiAudioSpeechModel
import org.springframework.ai.openai.api.OpenAiAudioApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.client.RestClient
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

/**
 * Bean Configuration
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Configuration
@Import(
    AwsConfig::class,
    ReloadableConfig::class,
    PassageConfig::class,
    BibleApiConfig::class,
    ChatGptConfig::class)
class Beans {

    @Bean
    fun restClient(): RestClient {
        return RestClient.create()
    }

    @Bean fun openAIClient(chat: ChatGptConfig): OpenAIClient {
        return OpenAIOkHttpClient.builder()
            .apiKey(chat.getApiKey()!!)
            .build()
    }

    @Bean fun openAiAudioSpeechModel(chat: ChatGptConfig): OpenAiAudioSpeechModel {
        val audioApi = OpenAiAudioApi.builder().apiKey(chat.getApiKey()!!).build()
        return OpenAiAudioSpeechModel(audioApi)
    }

    @Bean fun s3Client(aws: AwsConfig): S3Client {
        val credentials = AwsBasicCredentials.create(aws.getUsername(), aws.getPassword())

        return S3Client
            .builder()
            .region(Region.of(aws.getConfig()["region"]))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()
    }

    @Bean fun s3BucketService(aws: AwsConfig, s3: S3Client) = S3BucketService(aws, s3)

}