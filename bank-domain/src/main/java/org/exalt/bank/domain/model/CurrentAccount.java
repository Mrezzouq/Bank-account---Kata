package org.exalt.bank.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class CurrentAccount extends BankAccount {
    private final BigDecimal overdraftLimit;

    private CurrentAccount(Builder builder) {
        super(builder);
        overdraftLimit = builder.overdraftLimit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public Builder copy() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrentAccount that)) return false;
        return Objects.equals(overdraftLimit, that.overdraftLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(overdraftLimit);
    }

    @Override
    public String toString() {
        return "CurrentAccount{" +
                "overdraftLimit=" + overdraftLimit +
                '}';
    }

    public static final class Builder extends BankAccount.Builder<Builder> {
        private BigDecimal overdraftLimit;

        private Builder() {
        }

        private Builder(CurrentAccount currentAccount) {
            super(currentAccount);
            withOverdraftLimit(currentAccount.overdraftLimit);
        }

        public Builder withOverdraftLimit(BigDecimal overdraftLimit) {
            this.overdraftLimit = overdraftLimit;
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public CurrentAccount build() {
            return new CurrentAccount(this);
        }
    }
}
