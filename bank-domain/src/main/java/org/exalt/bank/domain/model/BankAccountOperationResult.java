package org.exalt.bank.domain.model;

import java.util.Objects;

public abstract class BankAccountOperationResult {
    private final String operationMessage;

    protected BankAccountOperationResult(String operationMessage) {
        this.operationMessage = operationMessage;
    }

    public String getOperationMessage() {
        return operationMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankAccountOperationResult that)) return false;
        return Objects.equals(operationMessage, that.operationMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationMessage);
    }

    @Override
    public String toString() {
        return "BankAccountOperationResult{" +
                "operationMessage='" + operationMessage + '\'' +
                '}';
    }

    public static final class Ok extends BankAccountOperationResult {
        public Ok(String operationMessage) {
            super(operationMessage);
        }
    }

    public static final class Failure extends BankAccountOperationResult {
        public Failure(String operationMessage) {
            super(operationMessage);
        }
    }

}
