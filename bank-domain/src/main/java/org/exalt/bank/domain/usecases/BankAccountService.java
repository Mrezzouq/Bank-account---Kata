package org.exalt.bank.domain.usecases;

import org.exalt.bank.domain.exceptions.BankOperationsException;
import org.exalt.bank.domain.model.BankAccount;
import org.exalt.bank.domain.model.CurrentAccount;
import org.exalt.bank.domain.model.SavingAccount;

import java.math.BigDecimal;

public class BankAccountService {
    public static final String ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE = "The amount cannot be null or negative";
    public static final String INSUFFICIENT_FUNDS_EXCEPTION_MESSAGE = "The withdrawal amount exceeds the available balance and the authorized overdraft limit.";
    public static final String DEPOSIT_EXCEEDS_LIMIT_EXCEPTION_MESSAGE = "Deposit exceeds the maximum allowed deposit limit for SavingAccount.";

    public BankAccount withdrawal(BankAccount account, BigDecimal amount) throws BankOperationsException {
        if (amount == null || BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE);
        }
        var overDraftLimit = BigDecimal.ZERO;
        if (account instanceof CurrentAccount currentAccount) overDraftLimit = currentAccount.getOverdraftLimit();
        var newBalance = account.getBalance().subtract(amount);

        if (newBalance.compareTo(overDraftLimit.negate()) < 0) {
            throw new BankOperationsException(INSUFFICIENT_FUNDS_EXCEPTION_MESSAGE);
        }

        return account.copy()
                .withBalance(newBalance)
                .build();
    }

    public BankAccount deposit(BankAccount account, BigDecimal amount) {
        if (amount == null || BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE);
        }

        if (account instanceof SavingAccount savingAccount) {
            var depositLimit = savingAccount.getDepositLimit();
            var newBalance = account.getBalance().add(amount);
            if (newBalance.compareTo(depositLimit) > 0) {
                throw new BankOperationsException(DEPOSIT_EXCEEDS_LIMIT_EXCEPTION_MESSAGE);
            }
        }

        return account.copy()
                .withBalance(account.getBalance().add(amount)).build();
    }
}
