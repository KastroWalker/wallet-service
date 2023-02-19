package dev.kastro.resource.repositories

import dev.kastro.application.config.DynamoDBConfig
import dev.kastro.application.exceptions.RepositoryException
import dev.kastro.domain.models.Wallet
import dev.kastro.domain.repositories.WalletRepository
import dev.kastro.resource.repositories.documents.WalletDocument
import jakarta.inject.Singleton
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException
import java.time.LocalDateTime

@Singleton
class WalletDocumentRepository(
    override var dynamoDbEnhancedClient: DynamoDbEnhancedClient,
    dynamoDBConfig: DynamoDBConfig,
) : WalletRepository, DocumentRepository(dynamoDBConfig.walletTableName) {
    private var table: DynamoDbTable<WalletDocument> = this.getTable()

    override fun save(wallet: Wallet): Wallet {
        return try {
            wallet.copy(
                createdAt = LocalDateTime.now()
            ).let {
                val document = WalletDocument(it)
                table.putItem(document)

                it
            }
        } catch (e: DynamoDbException) {
            throw RepositoryException("Error to save a new wallet.", e)
        }
    }

    override fun findByUserId(userId: String): Wallet? {
        try {
            val key = Key.builder().partitionValue(userId).build()

            return table.getItem { it.key(key) }?.toDomain()
        } catch (e: DynamoDbException) {
            throw RepositoryException("Error finding wallet by user id", e)
        }
    }
}