package org.exalt.bank.application.model;

import org.exalt.bank.domain.model.BankAccountStatement;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BankAccountStatementResponse {
    public static final BankAccountStatementResponse EMPTY = BankAccountStatementResponse.builder()
            .withAccountOperations(Collections.emptyList())
            .withAmountAtIssueDate(BigDecimal.ZERO).
            build();

    private final List<AccountOperationResponse> accountOperations;
    private final BigDecimal amountAtIssueDate;

    private BankAccountStatementResponse(Builder builder) {
        accountOperations = builder.accountOperations;
        amountAtIssueDate = builder.amountAtIssueDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static BankAccountStatementResponse from(BankAccountStatement bankAccountStatement) {
        return BankAccountStatementResponse.builder()
                .withAccountOperations(bankAccountStatement.getAccountOperations().stream()
                        .map(AccountOperationResponse::from)
                        .toList())
                .withAmountAtIssueDate(bankAccountStatement.getAmountAtIssueDate())
                .build();
    }

    public List<AccountOperationResponse> getAccountOperations() {
        return accountOperations;
    }

    public BigDecimal getAmountAtIssueDate() {
        return amountAtIssueDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankAccountStatementResponse that)) return false;
        return Objects.equals(accountOperations, that.accountOperations) && Objects.equals(amountAtIssueDate, that.amountAtIssueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountOperations, amountAtIssueDate);
    }

    @Override
    public String toString() {
        return "BankAccountStatementResponse{" +
                "accountOperations=" + accountOperations +
                ", amountAtIssueDate=" + amountAtIssueDate +
                '}';
    }

    public static final class Builder {
        private List<AccountOperationResponse> accountOperations;
        private BigDecimal amountAtIssueDate;

        private Builder() {}

        public Builder withAccountOperations(List<AccountOperationResponse> accountOperations) {
            this.accountOperations = accountOperations;
            return this;
        }

        public Builder withAmountAtIssueDate(BigDecimal amountAtIssueDate) {
            this.amountAtIssueDate = amountAtIssueDate;
            return this;
        }

        public BankAccountStatementResponse build() {
            return new BankAccountStatementResponse(this);
        }
    }
}
