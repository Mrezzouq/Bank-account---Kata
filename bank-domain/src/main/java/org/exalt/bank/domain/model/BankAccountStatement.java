package org.exalt.bank.domain.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class BankAccountStatement {
    private final List<AccountOperation> accountOperations;
    private final BigDecimal amountAtIssueDate;

    private BankAccountStatement(Builder builder) {
        accountOperations = builder.accountOperations;
        amountAtIssueDate = builder.amountAtIssueDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<AccountOperation> getAccountOperations() {
        return accountOperations;
    }

    public BigDecimal getAmountAtIssueDate() {
        return amountAtIssueDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankAccountStatement that)) return false;
        return Objects.equals(accountOperations, that.accountOperations) && Objects.equals(amountAtIssueDate, that.amountAtIssueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountOperations, amountAtIssueDate);
    }

    @Override
    public String toString() {
        return "BankAccountStatement{" +
                "accountOperations=" + accountOperations +
                ", amountAtIssueDate=" + amountAtIssueDate +
                '}';
    }

    public static final class Builder {
        private List<AccountOperation> accountOperations;
        private BigDecimal amountAtIssueDate;

        private Builder() {}

        public Builder withAccountOperations(List<AccountOperation> accountOperations) {
            this.accountOperations = accountOperations;
            return this;
        }

        public Builder withAmountAtIssueDate(BigDecimal amountAtIssueDate) {
            this.amountAtIssueDate = amountAtIssueDate;
            return this;
        }

        public BankAccountStatement build() {
            return new BankAccountStatement(this);
        }
    }
}
