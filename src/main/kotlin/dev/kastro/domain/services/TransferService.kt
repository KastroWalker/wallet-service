package dev.kastro.domain.services

import dev.kastro.application.exceptions.BadRequestException
import dev.kastro.domain.enums.Action
import dev.kastro.domain.message.TransferProducer
import dev.kastro.domain.models.Transfer
import jakarta.inject.Singleton
import java.util.UUID

@Singleton
class TransferService(
    private val walletService: WalletService,
    private val transferProducer: TransferProducer
) {
    fun execute(transfer: Transfer) {
        transfer.copy(id = UUID.randomUUID().toString()).let {
            val debitorAmount = walletService.getBalance(it.debtorId)

            if (debitorAmount < it.amount) {
                throw BadRequestException("Insufficient funds")
            }

            walletService.withdraw(it.debtorId, it.amount)
            transferProducer.produce(
                transferId = it.id!!,
                transfer = it,
                action = Action.CREATE
            )
        }
    }
}
