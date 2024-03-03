package org.exalt.bank.domain.port.out;

import org.exalt.bank.domain.model.AccountOperation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BankAccountStatementPort {
    List<AccountOperation> retrieveAccountOperations(UUID accountId, LocalDateTime dateOfIssue);
}
