package dev.kastro.resource.sqs

import com.fasterxml.jackson.databind.ObjectMapper
import dev.kastro.application.config.SqsConfig
import dev.kastro.application.exceptions.SqsException
import dev.kastro.domain.enums.Action
import dev.kastro.domain.message.TransferProducer
import dev.kastro.domain.models.Transfer
import jakarta.inject.Singleton
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

@Singleton
class TransferSqsProducer(
    private val sqsClient: SqsClient,
    private val sqsConfig: SqsConfig,
    private val objectMapper: ObjectMapper
) : TransferProducer {

    @SuppressWarnings("TooGenericExceptionCaught")
    override fun produce(
        transferId: String,
        transfer: Transfer,
        action: Action
    ) {
        val actionAttributeValue = buildMessageAttributeValue(action.name)
        val transferIdAttributeValue = buildMessageAttributeValue(transferId)

        val attributeValues = mapOf(
            "action" to actionAttributeValue,
            "transferId" to transferIdAttributeValue,
        )

        val messageBody = buildMessageBody(transfer)
        val messageRequest = buildFifoMessageRequest(messageBody, attributeValues)

        try {
            sqsClient.sendMessage(messageRequest)
        } catch (e: Exception) {
            println(e)
            throw SqsException("An error has just occurred in sending a new message to the queue.", e)
        }
    }

    private fun buildMessageAttributeValue(value: String) =
        MessageAttributeValue.builder()
            .dataType("String")
            .stringValue(value)
            .build()

    private fun buildMessageBody(transfer: Transfer): String {
        val transferMessage = TransferMapper.toMessage(transfer = transfer)
        return objectMapper.writeValueAsString(transferMessage)
    }

    private fun buildFifoMessageRequest(
        messageBody: String,
        attributeValue: Map<String, MessageAttributeValue>
    ) = SendMessageRequest.builder()
        .queueUrl(sqsConfig.transferQueue)
        .messageBody(messageBody)
        .messageAttributes(attributeValue)
        .messageGroupId("transfer-events")
        .build()
}
