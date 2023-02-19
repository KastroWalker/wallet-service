package dev.kastro.domain.services

import dev.kastro.application.exceptions.ResourceNotFoundException
import dev.kastro.domain.models.Wallet
import dev.kastro.domain.repositories.WalletRepository
import jakarta.inject.Singleton

@Singleton
class WalletService(
    private val walletRepository: WalletRepository
) {
    fun withdraw(userId: String, amount: Double) {
        val wallet = getWallet(userId)
        val newAmount = wallet.amount.minus(amount)
        walletRepository.save(wallet.copy(amount = newAmount))
    }

    fun getBalance(userId: String): Double = getWallet(userId).amount

    private fun getWallet(userId: String): Wallet =
        walletRepository.findByUserId(userId) ?: throw ResourceNotFoundException("Wallet with userId $userId not found")
}
