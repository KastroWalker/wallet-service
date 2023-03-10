package dev.kastro.domain.models

data class Transfer(
    val id: String? = null,
    val debtorId: String,
    val beneficiaryId: String,
    val amount: Double
)
