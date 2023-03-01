package dev.kastro.resource.sqs

import dev.kastro.domain.models.Transfer

object TransferMapper {
    fun toMessage(transfer: Transfer): TransferMessage {
        return TransferMessage(
            id = transfer.id,
            debtorId = transfer.debtorId,
            beneficiaryId = transfer.beneficiaryId,
            amount = transfer.amount
        )
    }
}
