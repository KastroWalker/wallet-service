package dev.kastro.domain.services

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.kastro.application.exceptions.BadRequestException
import dev.kastro.domain.message.TransferProducer
import dev.kastro.domain.models.Transfer
import io.micronaut.http.HttpStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TransferServiceTest {
    private val walletService = mockk<WalletService>()
    private val transferProducer = mockk<TransferProducer>()
    private val transferService = TransferService(walletService, transferProducer)

    @Nested
    @DisplayName("Execute transfer service tests")
    inner class TransferService {
        @Test
        fun `should execute a transfer when the transfer is valid`() {
            val transfer = Transfer(
                debtorId = "d96eb4b6-0139-4664-9b4c-ff8bcd62aaf2",
                beneficiaryId = "063b7e11-c61b-4045-a81a-6741f6710655",
                amount = 100.0
            )

            every { walletService.getBalance(transfer.debtorId) } returns 100.0
            every { walletService.withdraw(transfer.debtorId, transfer.amount) } returns Unit

            assertDoesNotThrow {
                transferService.execute(transfer)
            }

            verify(exactly = 1) { walletService.getBalance(transfer.debtorId) }
            verify(exactly = 1) { walletService.withdraw(transfer.debtorId, transfer.amount) }
        }

        @Test
        fun `should not execute a transfer when the found is insuficient`() {
            val exceptionMessage = "Insufficient funds"
            val transfer = Transfer(
                debtorId = "d96eb4b6-0139-4664-9b4c-ff8bcd62aaf2",
                beneficiaryId = "063b7e11-c61b-4045-a81a-6741f6710655",
                amount = 100.0
            )

            every { walletService.getBalance(transfer.debtorId) } returns 50.0

            val exception = assertThrows<BadRequestException> {
                transferService.execute(transfer)
            }

            assertThat(exception.message).isEqualTo(exceptionMessage)
            assertThat(exception.status).isEqualTo(HttpStatus.BAD_REQUEST)

            verify(exactly = 1) { walletService.getBalance(transfer.debtorId) }
            verify(exactly = 0) { walletService.withdraw(any(), any()) }
        }
    }
}
