package dev.kastro.domain.models

import java.time.LocalDateTime

data class Wallet(
    val userId: String,
    val amount: Double,
    val createdAt: LocalDateTime? = null
)