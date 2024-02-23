package org.exalt.bank.domain.usecases;

import org.exalt.bank.domain.exceptions.BalanceNotSufficientException;
import org.exalt.bank.domain.model.BankAccount;

import java.math.BigDecimal;
import java.util.Optional;

public class BankAccountService {
    public static final String ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE = "The amount cannot be null or negative";
    public static final String INSUFFICIENT_FUNDS_EXCEPTION_MESSAGE = "The withdrawal amount exceeds the available balance and the authorized overdraft limit.";

    public BankAccount withdrawal(BankAccount account, BigDecimal amount) throws BalanceNotSufficientException {
        if (amount == null || BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE);
        }
        var overDraftLimit = Optional.ofNullable(account.overdraftLimit()).orElse(BigDecimal.ZERO);
        var newBalance = account.balance().subtract(amount);

        if (newBalance.compareTo(overDraftLimit.negate()) < 0) {
            throw new BalanceNotSufficientException(INSUFFICIENT_FUNDS_EXCEPTION_MESSAGE);
        }

        return account.copy()
                .withBalance(newBalance)
                .build();
    }

    public BankAccount deposit(BankAccount account, BigDecimal amount) {
        if (amount == null || BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE);
        }

        return account.copy()
                .withBalance(account.balance().add(amount)).build();
    }
}
