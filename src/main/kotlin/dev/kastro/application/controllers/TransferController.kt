package dev.kastro.application.controllers

import dev.kastro.application.controllers.requests.TransferRequest
import dev.kastro.domain.services.TransferService
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post

@Controller("/transfers")
class TransferController(
    private val transferService: TransferService
) {
    @Post
    fun transfer(@Body request: TransferRequest) {
        transferService.execute(request.toDomain())
    }
}
