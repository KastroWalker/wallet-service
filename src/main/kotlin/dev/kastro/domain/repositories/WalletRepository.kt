package dev.kastro.domain.repositories

import dev.kastro.domain.models.Wallet

interface WalletRepository {
    fun save(wallet: Wallet): Wallet
    fun findByUserId(userId: String): Wallet?
}
