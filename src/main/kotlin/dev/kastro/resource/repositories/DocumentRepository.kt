package dev.kastro.resource.repositories

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

abstract class DocumentRepository(val tableName: String?) {

    abstract var dynamoDbEnhancedClient: DynamoDbEnhancedClient

    inline fun <reified T : Any> getTable(): DynamoDbTable<T> {
        return dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(T::class.java))
    }

    inline fun <reified T : Any> queryByFilter(index: String, attributeValue: AttributeValue): T? {
        val secIndex = this.getTable<T>().index(index)

        val queryConditional = QueryConditional
            .keyEqualTo(
                Key.builder().partitionValue(attributeValue)
                    .build()
            )

        val result = secIndex.query(
            QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build()
        ).iterator()

        val items = result.next().items()

        return if (items.isNotEmpty()) {
            items.first()
        } else {
            null
        }
    }
}
