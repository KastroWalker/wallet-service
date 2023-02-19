package dev.kastro.domain.services

import dev.kastro.application.exceptions.BadRequestException
import dev.kastro.domain.models.Transfer
import jakarta.inject.Singleton

@Singleton
class TransferService(private val walletService: WalletService) {
    fun execute(transfer: Transfer) {
        val debitorAmount = walletService.getBalance(transfer.debtorId)

        if (debitorAmount < transfer.amount) {
            throw BadRequestException("Insufficient funds")
        }

        walletService.withdraw(transfer.debtorId, transfer.amount)

        // Salvar no sqs para processar em background
    }
}