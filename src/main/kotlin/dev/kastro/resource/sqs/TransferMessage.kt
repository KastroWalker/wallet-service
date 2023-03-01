package dev.kastro.resource.sqs

data class TransferMessage(
    val id: String? = null,
    val debtorId: String,
    val beneficiaryId: String,
    val amount: Double
)
