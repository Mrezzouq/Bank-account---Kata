package org.exalt.bank.domain.exceptions;

public class AccountOperationsException extends RuntimeException {
    public AccountOperationsException(String message) {
        super(message);
    }
}
