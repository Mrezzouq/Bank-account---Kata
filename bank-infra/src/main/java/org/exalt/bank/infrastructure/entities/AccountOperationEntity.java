package org.exalt.bank.infrastructure.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exalt.bank.domain.model.AccountOperation;
import org.exalt.bank.infrastructure.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountOperationEntity {
    @Id
    private UUID operationId;
    private LocalDateTime operationDateTime;
    private BigDecimal amount;
    private OperationType operationType;
    private String accountType;
    @ManyToOne
    private BankAccountEntity bankAccount;

    private AccountOperationEntity(Builder builder) {
        setOperationId(builder.operationId);
        setOperationDateTime(builder.operationDateTime);
        setAmount(builder.amount);
        setOperationType(builder.operationType);
        setAccountType(builder.accountType);
        setBankAccount(builder.bankAccount);
    }

    public static Builder builder() {
        return new Builder();
    }

    public AccountOperation toAccountOperation() {
        return AccountOperation.builder()
                .withOperationId(this.operationId)
                .withOperationDateTime(this.operationDateTime)
                .withAmount(this.amount)
                .withOperationType(this.operationType.toDomainOperationType())
                .withAccountType(this.accountType)
                .build();
    }

    public static final class Builder {
        private UUID operationId;
        private LocalDateTime operationDateTime;
        private BigDecimal amount;
        private OperationType operationType;
        private String accountType;
        private BankAccountEntity bankAccount;

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

        public Builder withBankAccount(BankAccountEntity bankAccount) {
            this.bankAccount = bankAccount;
            return this;
        }

        public AccountOperationEntity build() {
            return new AccountOperationEntity(this);
        }
    }
}
