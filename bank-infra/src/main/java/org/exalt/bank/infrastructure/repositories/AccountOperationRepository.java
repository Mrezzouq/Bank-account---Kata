package org.exalt.bank.infrastructure.repositories;

import org.exalt.bank.infrastructure.entities.AccountOperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountOperationRepository extends JpaRepository<AccountOperationEntity, UUID> {
}
