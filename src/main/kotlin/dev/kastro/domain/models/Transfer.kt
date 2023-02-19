package dev.kastro.domain.models

data class Transfer(
    val debtorId: String,
    val beneficiaryId: String,
    val amount: Double
)
