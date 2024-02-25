package org.exalt.bank.domain.model;

import org.exalt.bank.domain.enums.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public abstract class BankAccount {
    private final UUID accountId;
    private final BigDecimal balance;
    private final AccountStatus status;
    private final LocalDate createdAt;

    protected <T extends Builder<T>> BankAccount(Builder<T> builder) {
        this.accountId = builder.accountId;
        this.balance = builder.balance;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
    }

    public abstract Builder<?> copy();

    public UUID getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankAccount that)) return false;
        return Objects.equals(accountId, that.accountId) &&
                Objects.equals(balance, that.balance) &&
                status == that.status &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, balance, status, createdAt);
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "accountId=" + accountId +
                ", balance=" + balance +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }

    public abstract static class Builder<T extends Builder<T>> {
        private UUID accountId;
        private BigDecimal balance;
        private AccountStatus status;
        private LocalDate createdAt;

        protected Builder() {
        }

        protected Builder(BankAccount bankAccount) {
            withAccountId(bankAccount.accountId);
            withBalance(bankAccount.balance);
            withStatus(bankAccount.status);
            withCreatedAt(bankAccount.createdAt);
        }

        protected abstract T getThis();

        public T withAccountId(UUID accountId) {
            this.accountId = accountId;
            return getThis();
        }

        public T withBalance(BigDecimal balance) {
            this.balance = balance;
            return getThis();
        }

        public T withStatus(AccountStatus status) {
            this.status = status;
            return getThis();
        }

        public T withCreatedAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return getThis();
        }

        public abstract BankAccount build();
    }
}
