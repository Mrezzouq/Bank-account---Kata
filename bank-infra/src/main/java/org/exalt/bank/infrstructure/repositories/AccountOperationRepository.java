package org.exalt.bank.infrstructure.repositories;

import org.exalt.bank.infrstructure.entities.AccountOperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountOperationRepository extends JpaRepository<AccountOperationEntity, UUID> {
}
