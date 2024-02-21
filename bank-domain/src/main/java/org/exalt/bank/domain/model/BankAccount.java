package org.exalt.bank.domain.model;

import org.exalt.bank.domain.enums.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BankAccount(
        UUID accountId,
        BigDecimal balance,
        AccountStatus status,
        LocalDate createdAt
) {

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static final class Builder {
        private UUID accountId;
        private BigDecimal balance;
        private AccountStatus status;
        private LocalDate createdAt;

        private Builder() {
        }

        private Builder(BankAccount bankAccount) {
            withAccountId(bankAccount.accountId());
            withBalance(bankAccount.balance());
            withStatus(bankAccount.status());
            withCreatedAt(bankAccount.createdAt);
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withAccountId(UUID accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder withBalance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder withStatus(AccountStatus status) {
            this.status = status;
            return this;
        }

        public Builder withCreatedAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BankAccount build() {
            return new BankAccount(accountId,
                    balance,
                    status,
                    createdAt);
        }
    }
}
