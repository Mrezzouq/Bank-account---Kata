package org.exalt.bank.infrastructure.enums;

public enum OperationType {
    DEPOSIT, WITHDRAWAL, UNKNOWN;

    public org.exalt.bank.domain.enums.OperationType toDomainOperationType() {
        return switch (this) {
            case DEPOSIT -> org.exalt.bank.domain.enums.OperationType.DEPOSIT;
            case WITHDRAWAL -> org.exalt.bank.domain.enums.OperationType.WITHDRAWAL;
            default -> org.exalt.bank.domain.enums.OperationType.UNKNOWN;
        };
    }
}
