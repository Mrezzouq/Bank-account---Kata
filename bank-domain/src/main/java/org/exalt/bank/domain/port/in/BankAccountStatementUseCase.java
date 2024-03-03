package org.exalt.bank.domain.port.in;

import org.exalt.bank.domain.model.BankAccountStatement;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountStatementUseCase {
    Optional<BankAccountStatement> retrieveAccountOperations(UUID accountId, LocalDateTime dateOfIssue);
}
