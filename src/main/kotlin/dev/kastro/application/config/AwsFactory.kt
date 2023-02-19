package dev.kastro.application.config

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.env.Environment
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import java.net.URI

@Factory
class AwsFactory(
    private val config: AwsConfig,
    private val environment: Environment
) {
    private val localEnvironments = setOf("local", "test")

    @Bean
    fun createDynamoDbClient(): DynamoDbClient {
        val dynamoDbClient = DynamoDbClient
            .builder()
            .region(Region.of(config.region))

        if (isLocalEnvironment(environment)) {
            setLocalConfig(dynamoDbClient)
        }

        return dynamoDbClient.build()
    }

    @Bean
    fun createDynamoDbEnhancedClient(): DynamoDbEnhancedClient {
        val dynamoDbClient = createDynamoDbClient()

        return DynamoDbEnhancedClient
            .builder()
            .dynamoDbClient(dynamoDbClient)
            .build()
    }

    @Bean
    fun createSecretsManagerClient(): SecretsManagerClient {
        val client = SecretsManagerClient
            .builder()
            .region(Region.of(config.region))

        if (isLocalEnvironment(environment)) {
            setLocalConfig(client)
        }

        return client.build()
    }

    private fun isLocalEnvironment(
        environment: Environment
    ) = environment.activeNames.any(localEnvironments::contains)

    private fun <BuilderT : AwsClientBuilder<BuilderT, ClientT>, ClientT> setLocalConfig(client: BuilderT) {
        client.credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(config.accessKeyId, config.secretKey)
            )
        ).endpointOverride(URI.create(config.endpoint!!))
    }
}
