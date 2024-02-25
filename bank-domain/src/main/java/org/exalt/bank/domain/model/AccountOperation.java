package org.exalt.bank.domain.model;

import org.exalt.bank.domain.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class AccountOperation {

    private final UUID operationId;
    private final LocalDateTime operationDateTime;
    private final BigDecimal amount;
    private final OperationType operationType;
    private final String accountType;

    private AccountOperation(Builder builder) {
        operationId = builder.operationId;
        operationDateTime = builder.operationDateTime;
        amount = builder.amount;
        operationType = builder.operationType;
        accountType = builder.accountType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getOperationId() {
        return operationId;
    }

    public LocalDateTime getOperationDateTime() {
        return operationDateTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public String getAccountType() {
        return accountType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountOperation that)) return false;
        return Objects.equals(operationId, that.operationId) &&
                Objects.equals(operationDateTime, that.operationDateTime) &&
                Objects.equals(amount, that.amount) &&
                operationType == that.operationType &&
                Objects.equals(accountType, that.accountType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId, operationDateTime, amount, operationType, accountType);
    }

    @Override
    public String toString() {
        return "AccountOperation{" +
                "operationId=" + operationId +
                ", operationDateTime=" + operationDateTime +
                ", amount=" + amount +
                ", operationType=" + operationType +
                ", accountType='" + accountType + '\'' +
                '}';
    }

    public static final class Builder {
        private UUID operationId;
        private LocalDateTime operationDateTime;
        private BigDecimal amount;
        private OperationType operationType;
        private String accountType;

        private Builder() {}

        public Builder withOperationId(UUID operationId) {
            this.operationId = operationId;
            return this;
        }

        public Builder withOperationDateTime(LocalDateTime operationDateTime) {
            this.operationDateTime = operationDateTime;
            return this;
        }

        public Builder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder withOperationType(OperationType operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder withAccountType(String accountType) {
            this.accountType = accountType;
            return this;
        }

        public AccountOperation build() {
            return new AccountOperation(this);
        }
    }
}
