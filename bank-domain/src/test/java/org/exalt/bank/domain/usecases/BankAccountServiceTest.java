package org.exalt.bank.domain.usecases;

import org.exalt.bank.domain.enums.AccountStatus;
import org.exalt.bank.domain.exceptions.BalanceNotSufficientException;
import org.exalt.bank.domain.model.BankAccount;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.exalt.bank.domain.usecases.BankAccountService.ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE;
import static org.exalt.bank.domain.usecases.BankAccountService.INSUFFICIENT_FUNDS_EXCEPTION_MESSAGE;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {
    @InjectMocks
    private BankAccountService bankAccountService;

    private static BankAccount buildBankAccount(UUID id, BigDecimal balance) {
        return BankAccount.builder()
                .withAccountId(id)
                .withBalance(balance)
                .withStatus(AccountStatus.ACTIVATED)
                .withCreatedAt(LocalDate.now())
                .withOverdraftLimit(new BigDecimal("500"))
                .build();
    }

    @Nested
    class DepositTest {

        private static Stream<Arguments> amountDepositProvider() {
            return Stream.of(
                    Arguments.of(new BigDecimal("3000"), new BigDecimal("500"), new BigDecimal("3500")),
                    Arguments.of(new BigDecimal("1000"), new BigDecimal("1500"), new BigDecimal("2500")),
                    Arguments.of(BigDecimal.ZERO, new BigDecimal("200"), new BigDecimal("200"))
            );
        }

        @ParameterizedTest
        @MethodSource("amountDepositProvider")
        void should_add_amount_to_balance_successfully(BigDecimal initialBalance, BigDecimal depositAmount, BigDecimal expected) {
            // Arrange
            var buildBankAccount = buildBankAccount(UUID.randomUUID(), initialBalance);

            // Act
            var actual = bankAccountService.deposit(buildBankAccount, depositAmount);

            // Assert
            assertThat(actual.balance()).isEqualTo(expected);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-10", "0", "null"})
        void should_throw_IllegalArgumentException_for_invalid_deposit_amounts(String input) {
            // Arrange
            var buildBankAccount = buildBankAccount(UUID.randomUUID(), new BigDecimal("300"));
            var amount = "null".equals(input) ? null : new BigDecimal(input);

            // Act
            ThrowingCallable actual = () -> bankAccountService.deposit(buildBankAccount, amount);

            // Assert
            assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE);
        }
    }

    @Nested
    class WithdrawalTest {

        private static Stream<Arguments> amountWithdrawalProvider() {
            return Stream.of(
                    Arguments.of(new BigDecimal("3000"), new BigDecimal("500"), new BigDecimal("2500")),
                    Arguments.of(new BigDecimal("1000"), new BigDecimal("1000"), BigDecimal.ZERO),
                    Arguments.of(new BigDecimal("2000"), new BigDecimal("2200"), new BigDecimal("-200"))
            );
        }

        @ParameterizedTest
        @MethodSource("amountWithdrawalProvider")
        void should_subtract_amount_from_balance_successfully(BigDecimal initialBalance, BigDecimal withdrawalAmount, BigDecimal expected) throws BalanceNotSufficientException {
            // Arrange
            var buildBankAccount = buildBankAccount(UUID.randomUUID(), initialBalance);

            // Act
            var actual = bankAccountService.withdrawal(buildBankAccount, withdrawalAmount);

            // Assert
            assertThat(actual.balance()).isEqualTo(expected);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-10", "-100", "-1000", "0", "null"})
        void should_throw_IllegalArgumentException_when_amount_to_withdrawal_is_negative(String input) {
            // Arrange
            var buildBankAccount = buildBankAccount(UUID.randomUUID(), new BigDecimal("300"));
            var amount = "null".equals(input) ? null : new BigDecimal(input);

            // Act
            ThrowingCallable actual = () -> bankAccountService.withdrawal(buildBankAccount, amount);

            // Assert
            assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE);
        }

        @Test
        void should_throw_BalanceNotSufficientException_when_withdrawal_exceeds_balance() {
            // Arrange
            var buildBankAccount = buildBankAccount(UUID.randomUUID(), new BigDecimal("300"));

            // Act
            ThrowingCallable actual = () -> bankAccountService.withdrawal(buildBankAccount, new BigDecimal("1500"));

            // Assert
            assertThatThrownBy(actual).isInstanceOf(BalanceNotSufficientException.class)
                    .hasMessage(INSUFFICIENT_FUNDS_EXCEPTION_MESSAGE);

        }
    }
}