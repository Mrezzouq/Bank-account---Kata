package org.exalt.bank.domain.exceptions;

public class BankOperationsException extends RuntimeException {
    public BankOperationsException(String message) {
        super(message);
    }
}
