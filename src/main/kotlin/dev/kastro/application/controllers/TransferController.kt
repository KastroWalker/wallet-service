package dev.kastro.application.controllers

import dev.kastro.domain.models.Transfer
import dev.kastro.domain.services.TransferService
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post

data class TransferRequest(
    val debtorId: String,
    val beneficiaryId: String,
    val amount: Double
)

fun TransferRequest.toDomain() = Transfer(
        debtorId = this.debtorId,
        beneficiaryId = this.beneficiaryId,
        amount = this.amount
    )

@Controller("/transfers")
class TransferController(
    private val transferService: TransferService
) {
    @Post
    fun transfer(@Body request: TransferRequest) {
        transferService.execute(request.toDomain())
    }
}