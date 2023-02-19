package dev.kastro.resource.repositories.documents

import dev.kastro.domain.models.Wallet
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.time.LocalDateTime

@DynamoDbBean
data class WalletDocument(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("user_id")
    var userId: String? = null,

    @get:DynamoDbAttribute("amount")
    var amount: Double? = null,

    @get:DynamoDbAttribute("created_at")
    var createdAt: LocalDateTime? = null
) {
    constructor(wallet: Wallet) : this() {
        userId = wallet.userId
        amount = wallet.amount
        createdAt = wallet.createdAt
    }

    fun toDomain(): Wallet = Wallet(
        userId = userId!!,
        amount = amount!!,
        createdAt = createdAt
    )
}