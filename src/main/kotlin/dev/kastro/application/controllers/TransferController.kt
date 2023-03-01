package dev.kastro.application.controllers

import dev.kastro.application.controllers.requests.TransferRequest
import dev.kastro.domain.services.TransferService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post

@Controller("/transfers")
class TransferController(
    private val transferService: TransferService
) {
    @Post
    fun transfer(@Body request: TransferRequest): MutableHttpResponse<Unit> {
        transferService.execute(request.toDomain())
        return HttpResponse.noContent()
    }
}
