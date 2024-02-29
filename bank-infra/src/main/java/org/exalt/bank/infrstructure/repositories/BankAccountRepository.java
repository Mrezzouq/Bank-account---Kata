package org.exalt.bank.infrstructure.repositories;

import org.exalt.bank.infrstructure.entities.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {
}
