package org.exalt.bank.infrastructure.adapter;

import org.exalt.bank.domain.exceptions.BankAccountNotFoundException;
import org.exalt.bank.domain.exceptions.BankOperationsException;
import org.exalt.bank.domain.model.BankAccountOperationResult;
import org.exalt.bank.infrastructure.entities.AccountOperationEntity;
import org.exalt.bank.infrastructure.entities.BankAccountEntity;
import org.exalt.bank.infrastructure.entities.CurrentAccountEntity;
import org.exalt.bank.infrastructure.entities.SavingAccountEntity;
import org.exalt.bank.infrastructure.enums.AccountStatus;
import org.exalt.bank.infrastructure.repositories.AccountOperationRepository;
import org.exalt.bank.infrastructure.repositories.BankAccountRepository;
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
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.exalt.bank.infrastructure.adapter.BankAccountOperationService.DEPOSIT_EXCEEDS_LIMIT_EXCEPTION_MESSAGE;
import static org.exalt.bank.infrastructure.adapter.BankAccountOperationService.INSUFFICIENT_FUNDS_EXCEPTION_MESSAGE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountOperationServiceTest {
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private AccountOperationRepository accountOperationRepository;
    @InjectMocks
    private BankAccountOperationService bankAccountOperationService;

    private static BankAccountEntity buildCurrentAccountEntity(UUID accountId, BigDecimal balance, BigDecimal overdraftLimit) {
        var currentAccount = new CurrentAccountEntity();
        currentAccount.setAccountId(accountId);
        currentAccount.setBalance(balance);
        currentAccount.setCreatedAt(LocalDate.now());
        currentAccount.setStatus(AccountStatus.ACTIVATED);
        currentAccount.setAccountOperations(Collections.emptyList());
        currentAccount.setOverdraftLimit(overdraftLimit);
        return currentAccount;
    }

    private static BankAccountEntity buildSavingAccountEntity(UUID accountId, BigDecimal balance, BigDecimal depositLimit) {
        var currentAccount = new SavingAccountEntity();
        currentAccount.setAccountId(accountId);
        currentAccount.setBalance(balance);
        currentAccount.setCreatedAt(LocalDate.now());
        currentAccount.setStatus(AccountStatus.ACTIVATED);
        currentAccount.setAccountOperations(Collections.emptyList());
        currentAccount.setDepositLimit(depositLimit);
        return currentAccount;
    }

    @Nested
    class Withdrawal {
        private static Stream<Arguments> validWithdrawalScenarios() {
            return Stream.of(
                    Arguments.of(
                            buildCurrentAccountEntity(UUID.randomUUID(), new BigDecimal("1000"), new BigDecimal("200")), new BigDecimal("1200"),
                            buildSavingAccountEntity(UUID.randomUUID(), new BigDecimal("1000"), new BigDecimal("10000")), new BigDecimal("1000")
                    )
            );
        }

        private static Stream<Arguments> invalidWithdrawalScenarios() {
            return Stream.of(
                    Arguments.of(
                            buildCurrentAccountEntity(UUID.randomUUID(), new BigDecimal("1000"), new BigDecimal("200")), new BigDecimal("12000"),
                            buildSavingAccountEntity(UUID.randomUUID(), new BigDecimal("1000"), new BigDecimal("10000")), new BigDecimal("100000")
                    )
            );
        }

        @ParameterizedTest
        @MethodSource("validWithdrawalScenarios")
        void should_succeed_withdrawal_when_conditions_are_valid(BankAccountEntity account, BigDecimal withdrawalAmount) {
            // Arrange
            when(bankAccountRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));

            // Act
            var actual = bankAccountOperationService.withdrawal(account.getAccountId(), withdrawalAmount);

            // Assert
            assertThat(actual).isEqualTo(new BankAccountOperationResult.Ok("WITHDRAWAL successful"));
            verify(accountOperationRepository, times(1)).save(any(AccountOperationEntity.class));
            verify(bankAccountRepository, times(1)).save(any(BankAccountEntity.class));
        }

        @ParameterizedTest
        @MethodSource("invalidWithdrawalScenarios")
        void should_return_failed_withdrawal_operation_when_funds_are_insufficient(BankAccountEntity account, BigDecimal withdrawalAmount) {
            // Arrange
            when(bankAccountRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));

            // Act
            var actual = bankAccountOperationService.withdrawal(account.getAccountId(), withdrawalAmount);

            // Assert
            assertThat(actual).isInstanceOf(BankAccountOperationResult.Failure.class);
            assertThat(actual.getOperationMessage()).isEqualTo(INSUFFICIENT_FUNDS_EXCEPTION_MESSAGE);
            verify(accountOperationRepository, never()).save(any(AccountOperationEntity.class));
        }

        @Test
        void should_throw_BankAccountNotFoundException_when_account_does_not_exist() {
            // Arrange
            UUID accountId = UUID.randomUUID();
            when(bankAccountRepository.findById(accountId)).thenReturn(Optional.empty());

            // Act
            ThrowingCallable actual = () -> bankAccountOperationService.withdrawal(accountId, any());

            // Assert
            assertThatThrownBy(actual).isInstanceOf(BankAccountNotFoundException.class)
                    .hasMessage("Bank account not found");
            verify(accountOperationRepository, never()).save(any(AccountOperationEntity.class));
        }
    }

    @Nested
    class Deposit {
        private static Stream<Arguments> successfulDepositScenarios() {
            return Stream.of(
                    Arguments.of(buildCurrentAccountEntity(UUID.randomUUID(), new BigDecimal("1000"), new BigDecimal("500")), new BigDecimal("200")),
                    Arguments.of(buildSavingAccountEntity(UUID.randomUUID(), new BigDecimal("5000"), new BigDecimal("10000")), new BigDecimal("4500"))
            );
        }

        private static Stream<Arguments> depositExceedsLimitScenarios() {
            return Stream.of(
                    Arguments.of(buildSavingAccountEntity(UUID.randomUUID(), new BigDecimal("8000"), new BigDecimal("10000")), new BigDecimal("2500"))
            );
        }

        @ParameterizedTest
        @MethodSource("successfulDepositScenarios")
        void should_succeed_in_deposit_when_conditions_are_valid(BankAccountEntity account, BigDecimal amountToDeposit) {
            // Arrange
            when(bankAccountRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));

            // Act
            var result = bankAccountOperationService.deposit(account.getAccountId(), amountToDeposit);

            // Assert
            assertThat(result).isEqualTo(new BankAccountOperationResult.Ok("DEPOSIT successful"));
            verify(accountOperationRepository).save(any(AccountOperationEntity.class));
            verify(bankAccountRepository).save(any(BankAccountEntity.class));
        }

        @ParameterizedTest
        @MethodSource("depositExceedsLimitScenarios")
        void should_throw_BankOperationException_when_deposit_exceeds_limit(SavingAccountEntity account, BigDecimal depositAmount) {
            // Arrange
            when(bankAccountRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));

            // Act
            ThrowingCallable actual = () -> bankAccountOperationService.deposit(account.getAccountId(), depositAmount);

            // Assert
            assertThatThrownBy(actual).isInstanceOf(BankOperationsException.class)
                    .hasMessage(DEPOSIT_EXCEEDS_LIMIT_EXCEPTION_MESSAGE);
            verify(accountOperationRepository, never()).save(any(AccountOperationEntity.class));
        }

        @Test
        void should_throw_BankAccountNotFoundException_when_account_does_not_exist() {
            // Arrange
            UUID accountId = UUID.randomUUID();
            when(bankAccountRepository.findById(accountId)).thenReturn(Optional.empty());

            // Act
            ThrowingCallable actual = () -> bankAccountOperationService.deposit(accountId, any());

            // Assert
            assertThatThrownBy(actual).isInstanceOf(BankAccountNotFoundException.class)
                    .hasMessage("Bank account not found");
            verify(accountOperationRepository, never()).save(any(AccountOperationEntity.class));
        }
    }
}