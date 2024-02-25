package org.exalt.bank.domain.usecases;


import org.exalt.bank.domain.enums.AccountStatus;
import org.exalt.bank.domain.exceptions.BankOperationsException;
import org.exalt.bank.domain.model.BankAccount;
import org.exalt.bank.domain.model.CurrentAccount;
import org.exalt.bank.domain.model.SavingAccount;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.exalt.bank.domain.usecases.BankAccountService.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {
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
                    Arguments.of(buildCurrentAccount(new BigDecimal("3000")), new BigDecimal("500"), new BigDecimal("3500")),
                    Arguments.of(buildCurrentAccount(new BigDecimal("1000")), new BigDecimal("1500"), new BigDecimal("2500")),
                    Arguments.of(buildSavingAccount(new BigDecimal("2000")), new BigDecimal("2000"), new BigDecimal("4000")
                    )
            );
        }

        private static Stream<Arguments> invalidDepositAmountsForAccountTypesProvider() {
            return Stream.of(
                    Arguments.of(buildCurrentAccount(new BigDecimal("300")), new BigDecimal("-10")),
                    Arguments.of(buildCurrentAccount(new BigDecimal("300")), BigDecimal.ZERO),
                    Arguments.of(buildCurrentAccount(new BigDecimal("300")), null),
                    Arguments.of(buildSavingAccount(new BigDecimal("300")), new BigDecimal("-10")),
                    Arguments.of(buildSavingAccount(new BigDecimal("300")), BigDecimal.ZERO),
                    Arguments.of(buildSavingAccount(new BigDecimal("300")), null)
            );
        }

        @ParameterizedTest
        @MethodSource("depositScenariosForDifferentAccountTypesProvider")
        void should_add_amount_to_balance_successfully(BankAccount account, BigDecimal depositAmount, BigDecimal expected) {
            // Act
            var actual = bankAccountService.deposit(account, depositAmount);

            // Assert
            var expectedAccount = account.copy().withBalance(expected).build();
            assertThat(actual).isEqualTo(expectedAccount);
        }

        @ParameterizedTest
        @MethodSource("invalidDepositAmountsForAccountTypesProvider")
        void should_throw_IllegalArgumentException_for_invalid_deposit_amounts(BankAccount account, BigDecimal input) {

            // Act
            ThrowingCallable actual = () -> bankAccountService.deposit(account, input);

            // Assert
            assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE);
        }

        @Test
        void should_throw_BankOperationsException_when_deposit_exceeds_saving_account_limit() {
            // Arrange
            var savingAccount = buildSavingAccount(new BigDecimal("9000"));

            // Act
            ThrowingCallable actual = () -> bankAccountService.deposit(savingAccount, new BigDecimal("1001"));

            // Assert
            assertThatThrownBy(actual).isInstanceOf(BankOperationsException.class)
                    .hasMessage(DEPOSIT_EXCEEDS_LIMIT_EXCEPTION_MESSAGE);
        }
    }

    @Nested
    class WithdrawalTest {

        private static Stream<Arguments> withdrawalScenariosForDifferentAccountTypesProvider() {
            return Stream.of(
                    Arguments.of(buildCurrentAccount(new BigDecimal("3000")), new BigDecimal("500"), new BigDecimal("2500")),
                    Arguments.of(buildCurrentAccount(new BigDecimal("1000")), new BigDecimal("1000"), BigDecimal.ZERO),
                    Arguments.of(buildCurrentAccount(new BigDecimal("2000")), new BigDecimal("2200"), new BigDecimal("-200")),
                    Arguments.of(buildSavingAccount(new BigDecimal("2000")), new BigDecimal("2001"), new BigDecimal("1800"))
            );
        }

        private static Stream<Arguments> withdrawalAmountsExceedingBalanceProvider() {
            return Stream.of(
                    Arguments.of(buildCurrentAccount(new BigDecimal("3000")), new BigDecimal("3501")),
                    Arguments.of(buildSavingAccount(new BigDecimal("2000")), new BigDecimal("2001"))
            );
        }

        private static Stream<Arguments> withdrawalNegativeAmountsProvider() {
            return Stream.of(
                    Arguments.of(buildCurrentAccount(new BigDecimal("300")), new BigDecimal("-10")),
                    Arguments.of(buildCurrentAccount(new BigDecimal("300")), new BigDecimal("-100")),
                    Arguments.of(buildSavingAccount(new BigDecimal("300")), new BigDecimal("-1000")),
                    Arguments.of(buildSavingAccount(new BigDecimal("300")), BigDecimal.ZERO),
                    Arguments.of(buildSavingAccount(new BigDecimal("300")), null)
            );
        }

        @ParameterizedTest
        @MethodSource("withdrawalScenariosForDifferentAccountTypesProvider")
        void should_subtract_amount_from_balance_successfully(BankAccount account, BigDecimal withdrawalAmount, BigDecimal expected) {
            // Act
            var actual = bankAccountService.withdrawal(account, withdrawalAmount);

            // Assert
            var expectedAccount = account.copy().withBalance(expected).build();
            assertThat(actual).isEqualTo(expectedAccount);
        }

        @ParameterizedTest
        @MethodSource("withdrawalNegativeAmountsProvider")
        void should_throw_IllegalArgumentException_when_amount_to_withdrawal_is_negative(BankAccount account, BigDecimal input) {
            // Act
            ThrowingCallable actual = () -> bankAccountService.withdrawal(account, input);

            // Assert
            assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE);
        }

        @ParameterizedTest
        @MethodSource("withdrawalAmountsExceedingBalanceProvider")
        void should_throw_BankOperationsException_when_withdrawal_exceeds_balance(BankAccount account, BigDecimal withdrawalAmount) {
            // Act
            ThrowingCallable actual = () -> bankAccountService.withdrawal(account, withdrawalAmount);

            // Assert
            assertThatThrownBy(actual).isInstanceOf(BankOperationsException.class)
                    .hasMessage(INSUFFICIENT_FUNDS_EXCEPTION_MESSAGE);

        }
    }
}