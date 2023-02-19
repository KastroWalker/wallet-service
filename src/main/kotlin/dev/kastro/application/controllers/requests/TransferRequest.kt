package dev.kastro.application.controllers.requests

import dev.kastro.domain.models.Transfer

data class TransferRequest(
    val debtorId: String,
    val beneficiaryId: String,
    val amount: Double
) {
    fun toDomain() = Transfer(
        debtorId = this.debtorId,
        beneficiaryId = this.beneficiaryId,
        amount = this.amount
    )
}
