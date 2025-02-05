package org.exalt.bank.infrastructure.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.exalt.bank.infrastructure.enums.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class BankAccountEntity {
    @Id
    private UUID accountId;
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private LocalDate createdAt;
    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    private List<AccountOperationEntity> accountOperations;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankAccountEntity that)) return false;
        return Objects.equals(accountId, that.accountId) && Objects.equals(balance, that.balance) && status == that.status && Objects.equals(createdAt, that.createdAt) && Objects.equals(accountOperations, that.accountOperations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, balance, status, createdAt, accountOperations);
    }

    @Override
    public String toString() {
        return "BankAccountEntity{" +
                "accountId=" + accountId +
                ", balance=" + balance +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", accountOperations=" + accountOperations +
                '}';
    }
}
