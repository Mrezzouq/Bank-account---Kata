package org.exalt.bank.infrstructure.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exalt.bank.domain.model.AccountOperation;
import org.exalt.bank.infrstructure.enums.OperationType;

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

    public AccountOperation toAccountOperation() {
        return AccountOperation.builder()
                .withOperationId(this.operationId)
                .withOperationDateTime(this.operationDateTime)
                .withAmount(this.amount)
                .withOperationType(this.operationType.toDomainOperationType())
                .withAccountType(this.accountType)
                .build();
    }

}
