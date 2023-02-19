package dev.kastro.application.controllers

import assertk.assertThat
import dev.kastro.application.controllers.requests.TransferRequest
import dev.kastro.domain.services.TransferService
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import assertk.assertions.isEqualTo
import dev.kastro.application.exceptions.ResourceNotFoundException
import io.micronaut.http.HttpStatus
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TransferControllerTest {
    private val transferService = mockk<TransferService>(relaxed = true)
    private val controller = TransferController(transferService)

    @Nested
    @DisplayName("Create transfer controller tests")
    inner class CreateTransfer {
        @Test
        fun `should create a transfer when the request is valid`() {
            val request = TransferRequest(
                debtorId = "",
                beneficiaryId = "",
                amount = 50.0,
            )

            val result = controller.transfer(request)

            assertThat(result.code()).isEqualTo(HttpStatus.NO_CONTENT.code)

            verify { controller.transfer(request) }
            verify(exactly = 1){ transferService.execute(request.toDomain()) }
        }

        @Test
        fun `should not create a transfer when the request is invalid`() {
            val request = TransferRequest(
                debtorId = "invalidId",
                beneficiaryId = "d96eb4b6-0139-4664-9b4c-ff8bcd62aaf2",
                amount = 100.0,
            )
            val exceptionMessage = "Wallet with userId ${request.debtorId} not found"

            every { transferService.execute(request.toDomain()) } throws
                ResourceNotFoundException(exceptionMessage)

            val exception = assertThrows<ResourceNotFoundException> {
                 controller.transfer(request)
            }

            assertThat(exception.status).isEqualTo(HttpStatus.NOT_FOUND)
            assertThat(exception.message).isEqualTo(exceptionMessage)

            verify(exactly = 1){ transferService.execute(request.toDomain()) }
        }
    }
}