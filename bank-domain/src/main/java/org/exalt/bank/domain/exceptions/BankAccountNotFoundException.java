package org.exalt.bank.domain.exceptions;

public class BankAccountNotFoundException extends RuntimeException{
    public BankAccountNotFoundException(String message) {
        super(message);
    }
}
