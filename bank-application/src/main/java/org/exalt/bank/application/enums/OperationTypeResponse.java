package org.exalt.bank.application.enums;

import org.exalt.bank.domain.enums.OperationType;

public enum OperationTypeResponse {
    DEPOSIT, WITHDRAWAL, UNKNOWN;

    public static OperationTypeResponse from(OperationType operationType) {
        return switch (operationType) {
            case DEPOSIT -> DEPOSIT;
            case WITHDRAWAL -> WITHDRAWAL;
            default -> UNKNOWN;
        };
    }
}
