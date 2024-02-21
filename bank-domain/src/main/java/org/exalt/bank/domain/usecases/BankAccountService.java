package org.exalt.bank.domain.usecases;

import org.exalt.bank.domain.exceptions.BalanceNotSufficientException;
import org.exalt.bank.domain.model.BankAccount;

import java.math.BigDecimal;

public class BankAccountService {
    public static final String ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE = "The amount cannot be null or negative";
    public static final String BALANCE_NOT_SUFFICIENT_EXCEPTION_MESSAGE = "The balance is insufficient to make the withdrawal";

    public BankAccount withdrawal(BankAccount account, BigDecimal amount) throws BalanceNotSufficientException {
        validateWithdrawalAmount(account, amount);

        return account.copy()
                .withBalance(account.balance().subtract(amount)).build();
    }

    public BankAccount deposit(BankAccount account, BigDecimal amount) {
        if (amount == null || BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE);
        }

        return account.copy()
                .withBalance(account.balance().add(amount)).build();
    }

    private void validateWithdrawalAmount(BankAccount account, BigDecimal amount) throws BalanceNotSufficientException {
        if (amount == null || BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE);
        }

        if (account.balance().compareTo(amount) < 0) {
            throw new BalanceNotSufficientException(BALANCE_NOT_SUFFICIENT_EXCEPTION_MESSAGE);
        }
    }
}
