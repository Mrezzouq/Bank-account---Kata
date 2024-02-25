package org.exalt.bank.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class SavingAccount extends BankAccount {
    private final BigDecimal depositLimit;

    private SavingAccount(Builder builder) {
        super(builder);
        depositLimit = builder.depositLimit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public BigDecimal getDepositLimit() {
        return depositLimit;
    }

    @Override
    public Builder copy() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavingAccount that)) return false;
        return Objects.equals(depositLimit, that.depositLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(depositLimit);
    }

    @Override
    public String toString() {
        return "SavingAccount{" +
                "depositLimit=" + depositLimit +
                '}';
    }

    public static final class Builder extends BankAccount.Builder<Builder> {
        private BigDecimal depositLimit;

        private Builder() {
        }

        private Builder(SavingAccount savingAccount) {
            super(savingAccount);
            withDepositLimit(savingAccount.depositLimit);
        }

        public Builder withDepositLimit(BigDecimal depositLimit) {
            this.depositLimit = depositLimit;
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SavingAccount build() {
            return new SavingAccount(this);
        }


    }
}
