package dev.kastro.application.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("aws.dynamodb")
class DynamoDBConfig {
    var walletTableName: String = "wallet-service.wallets"
}
