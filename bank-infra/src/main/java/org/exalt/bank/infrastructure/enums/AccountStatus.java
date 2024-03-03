package org.exalt.bank.infrastructure.enums;

public enum AccountStatus {
    CREATED, ACTIVATED, SUSPENDED, UNKNOWN;

    public org.exalt.bank.domain.enums.AccountStatus toDomainAccountStatus() {
        return switch (this) {
            case CREATED -> org.exalt.bank.domain.enums.AccountStatus.CREATED;
            case ACTIVATED -> org.exalt.bank.domain.enums.AccountStatus.ACTIVATED;
            case SUSPENDED -> org.exalt.bank.domain.enums.AccountStatus.SUSPENDED;
            default -> org.exalt.bank.domain.enums.AccountStatus.UNKNOWN;
        };
    }
}
