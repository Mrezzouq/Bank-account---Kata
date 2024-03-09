package org.exalt.bank.infrastructure.repositories;

import org.exalt.bank.infrastructure.entities.AccountOperationEntity;
import org.exalt.bank.infrastructure.entities.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {
    @Query("SELECT op FROM AccountOperationEntity op WHERE op.bankAccount.accountId = :accountId AND op.operationDateTime BETWEEN :startDate AND :endDate ORDER BY op.operationDateTime DESC")
    List<AccountOperationEntity> findOperationsByAccountId(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);
}
