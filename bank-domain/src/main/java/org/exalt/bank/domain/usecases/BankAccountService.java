package org.exalt.bank.domain.usecases;

import org.exalt.bank.domain.exceptions.BankAccountNotFoundException;
import org.exalt.bank.domain.exceptions.BankOperationsException;
import org.exalt.bank.domain.model.BankAccountOperationResult;
import org.exalt.bank.domain.port.in.BankAccountOperationUseCase;
import org.exalt.bank.domain.port.out.BankAccountOperationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;

public class BankAccountService implements BankAccountOperationUseCase {
    static final String ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE = "bank account not found";
    static final String OPERATION_FAILED_EXCEPTION_MESSAGE = "Operation failed due to a bank operation error";
    private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountService.class);
    private final BankAccountOperationPort bankAccountOperationPort;

    public BankAccountService(BankAccountOperationPort bankAccountOperationPort) {
        this.bankAccountOperationPort = bankAccountOperationPort;
    }

    @Override
    public BankAccountOperationResult withdrawal(UUID accountId, BigDecimal amount) {
        try {
            return this.bankAccountOperationPort.withdrawal(accountId, amount);
        } catch (BankAccountNotFoundException e) {
            LOGGER.warn("Bank account not found for ID {}", accountId);
            return new BankAccountOperationResult.Failure(ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public BankAccountOperationResult deposit(UUID accountId, BigDecimal amount) {
        try {
            return this.bankAccountOperationPort.deposit(accountId, amount);
        } catch (BankOperationsException e) {
            LOGGER.warn("Operation failed for bank account with ID {}: {}", accountId, e.getMessage());
            return new BankAccountOperationResult.Failure(OPERATION_FAILED_EXCEPTION_MESSAGE);
        } catch (BankAccountNotFoundException e) {
            LOGGER.warn("Bank account not found for ID {}", accountId);
            return new BankAccountOperationResult.Failure(ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }
}
