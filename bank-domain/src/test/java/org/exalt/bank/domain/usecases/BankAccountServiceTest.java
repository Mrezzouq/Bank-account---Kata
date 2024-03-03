package org.exalt.bank.domain.usecases;


import org.exalt.bank.domain.enums.AccountStatus;
import org.exalt.bank.domain.exceptions.BankAccountNotFoundException;
import org.exalt.bank.domain.exceptions.BankOperationsException;
import org.exalt.bank.domain.model.BankAccount;
import org.exalt.bank.domain.model.BankAccountOperationResult;
import org.exalt.bank.domain.model.CurrentAccount;
import org.exalt.bank.domain.model.SavingAccount;
import org.exalt.bank.domain.port.out.BankAccountOperationPort;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.exalt.bank.domain.usecases.BankAccountService.ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE;
import static org.exalt.bank.domain.usecases.BankAccountService.OPERATION_FAILED_EXCEPTION_MESSAGE;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {
    @Mock
    private BankAccountOperationPort bankAccountOperationPort;
    @InjectMocks
    private BankAccountService bankAccountService;

    private static BankAccount buildCurrentAccount(BigDecimal balance) {
        return CurrentAccount.builder()
                .withAccountId(UUID.randomUUID())
                .withBalance(balance)
                .withStatus(AccountStatus.ACTIVATED)
                .withCreatedAt(LocalDate.now())
                .withOverdraftLimit(new BigDecimal("500"))
                .build();
    }

    private static BankAccount buildSavingAccount(BigDecimal balance) {
        return SavingAccount.builder()
                .withAccountId(UUID.randomUUID())
                .withBalance(balance)
                .withStatus(AccountStatus.ACTIVATED)
                .withCreatedAt(LocalDate.now())
                .withDepositLimit(new BigDecimal("10000"))
                .build();
    }

    @Nested
    class DepositTest {

        private static Stream<Arguments> depositScenariosForDifferentAccountTypesProvider() {
            return Stream.of(
                    Arguments.of(buildCurrentAccount(new BigDecimal("3000")), new BigDecimal("500")),
                    Arguments.of(buildCurrentAccount(new BigDecimal("1000")), new BigDecimal("1500")),
                    Arguments.of(buildSavingAccount(new BigDecimal("2000")), new BigDecimal("2000"))
            );
        }

        @ParameterizedTest
        @MethodSource("depositScenariosForDifferentAccountTypesProvider")
        void should_add_amount_to_balance_successfully(BankAccount account, BigDecimal depositAmount) {
            // Arrange
            when(bankAccountOperationPort.deposit(account.getAccountId(), depositAmount)).thenReturn(new BankAccountOperationResult.Ok("DEPOSIT successful"));

            // Act
            var actual = bankAccountService.deposit(account.getAccountId(), depositAmount);

            // Assert
            assertThat(actual).isInstanceOf(BankAccountOperationResult.Ok.class);
        }

        @Test
        void Should_throw_BankAccountNotFoundException_when_no_account_is_found() {
            // Arrange
            when(bankAccountOperationPort.deposit(any(), any())).thenThrow(new BankAccountNotFoundException(ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE));

            // Act
            var actual = bankAccountService.deposit(any(), any());

            // Assert
            assertThat(actual).isEqualTo(new BankAccountOperationResult.Failure(ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE));
        }

        @Test
        void Should_throw_BankOperationsException_when_deposit_amount_exceeds_deposit_limit() {
            // Arrange
            when(bankAccountOperationPort.deposit(any(), any())).thenThrow(new BankOperationsException(OPERATION_FAILED_EXCEPTION_MESSAGE));

            // Act
            var actual = bankAccountService.deposit(any(), any());

            // Assert
            assertThat(actual).isEqualTo(new BankAccountOperationResult.Failure(OPERATION_FAILED_EXCEPTION_MESSAGE));
        }
    }

    @Nested
    class WithdrawalTest {

        private static Stream<Arguments> withdrawalScenariosForDifferentAccountTypesProvider() {
            return Stream.of(
                    Arguments.of(buildCurrentAccount(new BigDecimal("3000")), new BigDecimal("500")),
                    Arguments.of(buildCurrentAccount(new BigDecimal("1000")), new BigDecimal("1000")),
                    Arguments.of(buildCurrentAccount(new BigDecimal("2000")), new BigDecimal("2200")),
                    Arguments.of(buildSavingAccount(new BigDecimal("2000")), new BigDecimal("200"))
            );
        }

        @ParameterizedTest
        @MethodSource("withdrawalScenariosForDifferentAccountTypesProvider")
        void should_subtract_amount_from_balance_successfully(BankAccount account, BigDecimal withdrawalAmount) {
            // Arrange
            when(bankAccountOperationPort.withdrawal(account.getAccountId(), withdrawalAmount)).thenReturn(new BankAccountOperationResult.Ok("WITHDRAWAL successful"));

            // Act
            var actual = bankAccountService.withdrawal(account.getAccountId(), withdrawalAmount);

            // Assert
            assertThat(actual).isEqualTo(new BankAccountOperationResult.Ok("WITHDRAWAL successful"));
        }

        @Test
        void Should_throw_BankAccountNotFoundException_when_no_account_is_found() {
            // Arrange
            when(bankAccountOperationPort.withdrawal(any(), any())).thenThrow(new BankAccountNotFoundException(ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE));

            // Act
            var actual = bankAccountService.withdrawal(any(), any());

            // Assert
            assertThat(actual).isEqualTo(new BankAccountOperationResult.Failure(ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE));

        }
    }
}