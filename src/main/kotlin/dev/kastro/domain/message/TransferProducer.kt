package dev.kastro.domain.message

import dev.kastro.domain.enums.Action
import dev.kastro.domain.models.Transfer

interface TransferProducer {
    fun produce(
        transferId: String,
        transfer: Transfer,
        action: Action
    )
}
