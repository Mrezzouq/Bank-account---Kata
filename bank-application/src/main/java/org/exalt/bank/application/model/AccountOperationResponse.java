package org.exalt.bank.application.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.exalt.bank.application.enums.OperationTypeResponse;
import org.exalt.bank.domain.model.AccountOperation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class AccountOperationResponse {
    private final UUID operationId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime operationDateTime;
    private final BigDecimal amount;
    private final OperationTypeResponse operationType;
    private final String accountType;

    private AccountOperationResponse(Builder builder) {
        operationId = builder.operationId;
        operationDateTime = builder.operationDateTime;
        amount = builder.amount;
        operationType = builder.operationType;
        accountType = builder.accountType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static AccountOperationResponse from(AccountOperation accountOperation) {
        return AccountOperationResponse.builder()
                .withOperationId(accountOperation.getOperationId())
                .withOperationDateTime(accountOperation.getOperationDateTime())
                .withAmount(accountOperation.getAmount())
                .withAccountType(accountOperation.getAccountType())
                .withOperationType(OperationTypeResponse.from(accountOperation.getOperationType()))
                .build();
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

    public OperationTypeResponse getOperationType() {
        return operationType;
    }

    public String getAccountType() {
        return accountType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountOperationResponse that)) return false;
        return Objects.equals(operationId, that.operationId) && Objects.equals(operationDateTime, that.operationDateTime) && Objects.equals(amount, that.amount) && operationType == that.operationType && Objects.equals(accountType, that.accountType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId, operationDateTime, amount, operationType, accountType);
    }

    @Override
    public String toString() {
        return "AccountOperationResponse{" +
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
        private OperationTypeResponse operationType;
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

        public Builder withOperationType(OperationTypeResponse operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder withAccountType(String accountType) {
            this.accountType = accountType;
            return this;
        }

        public AccountOperationResponse build() {
            return new AccountOperationResponse(this);
        }
    }
}
