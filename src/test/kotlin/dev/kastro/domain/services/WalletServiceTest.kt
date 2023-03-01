package dev.kastro.domain.services

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.kastro.application.exceptions.ResourceNotFoundException
import dev.kastro.domain.models.Wallet
import dev.kastro.domain.repositories.WalletRepository
import io.micronaut.http.HttpStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class WalletServiceTest {
    private val walletRepository = mockk<WalletRepository>()
    private val walletService = WalletService(walletRepository)

    @Nested
    @DisplayName("Get balance tests")
    inner class GetBalanceTests {
        @Test
        fun `should return balance when wallet with userId exists`() {
            val userId = "d96eb4b6-0139-4664-9b4c-ff8bcd62aaf2"
            val walletResponse = Wallet(
                userId = userId,
                amount = 100.0
            )

            every { walletRepository.findByUserId(userId) } returns walletResponse

            val balance = walletService.getBalance(userId)

            assertThat(balance).isEqualTo(walletResponse.amount)

            verify(exactly = 1) { walletRepository.findByUserId(userId) }
        }

        @Test
        fun `should throw exception when wallet with userId does not exist`() {
            val userId = "d96eb4b6-0139-4664-9b4c-ff8bcd62aaf2"
            val exceptionMessage = "Wallet with userId $userId not found"

            every { walletRepository.findByUserId(userId) } returns null

            val exception = assertThrows<ResourceNotFoundException> {
                walletService.getBalance(userId)
            }

            assertThat(exception.message).isEqualTo(exceptionMessage)
            assertThat(exception.status).isEqualTo(HttpStatus.NOT_FOUND)

            verify(exactly = 1) { walletRepository.findByUserId(userId) }
        }
    }

    @Nested
    @DisplayName("Withdraw tests")
    inner class WithdrawTests {
        @Test
        fun `should withdraw when wallet with userId exists`() {
            val userId = "d96eb4b6-0139-4664-9b4c-ff8bcd62aaf2"
            val amount = 100.0
            val walletResponse = Wallet(
                userId = userId,
                amount = 100.0
            )
            val newWalletResponse = walletResponse.copy(amount = amount - walletResponse.amount)

            every { walletRepository.findByUserId(userId) } returns walletResponse
            every { walletRepository.save(any()) } returns newWalletResponse

            walletService.withdraw(userId, amount)

            verify(exactly = 1) { walletRepository.findByUserId(userId) }
            verify(exactly = 1) { walletRepository.save(any()) }
        }

        @Test
        fun `should throw exception when wallet with userId does not exist`() {
            val userId = "d96eb4b6-0139-4664-9b4c-ff8bcd62aaf2"
            val amount = 100.0
            val exceptionMessage = "Wallet with userId $userId not found"

            every { walletRepository.findByUserId(userId) } returns null

            val exception = assertThrows<ResourceNotFoundException> {
                walletService.withdraw(userId, amount)
            }

            assertThat(exception.message).isEqualTo(exceptionMessage)
            assertThat(exception.status).isEqualTo(HttpStatus.NOT_FOUND)

            verify(exactly = 1) { walletRepository.findByUserId(userId) }
            verify(exactly = 0) { walletRepository.save(any()) }
        }
    }
}
