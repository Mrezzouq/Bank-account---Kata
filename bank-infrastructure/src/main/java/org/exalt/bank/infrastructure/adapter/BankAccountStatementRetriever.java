package org.exalt.bank.infrastructure.adapter;

import org.exalt.bank.domain.exceptions.AccountOperationsException;
import org.exalt.bank.domain.model.AccountOperation;
import org.exalt.bank.domain.port.out.BankAccountStatementPort;
import org.exalt.bank.infrastructure.entities.AccountOperationEntity;
import org.exalt.bank.infrastructure.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BankAccountStatementRetriever implements BankAccountStatementPort {
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountStatementRetriever(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public List<AccountOperation> retrieveAccountOperations(UUID accountId, LocalDateTime dateOfIssue) {
        var startDate = dateOfIssue.minusDays(31);
        var operations = bankAccountRepository.findOperationsByAccountId(accountId, startDate, dateOfIssue);

        if (operations.isEmpty()) {
            throw new AccountOperationsException("No operations found for the bank account with ID: " + accountId + " within the date range: " + startDate + " to " + dateOfIssue + ".");
        }

        return operations.stream()
                .map(AccountOperationEntity::toAccountOperation)
                .toList();
    }
}
