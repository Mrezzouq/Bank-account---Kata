package org.exalt.bank.domain.usecases;

import org.exalt.bank.domain.enums.OperationType;
import org.exalt.bank.domain.exceptions.AccountOperationsException;
import org.exalt.bank.domain.model.AccountOperation;
import org.exalt.bank.domain.model.BankAccountStatement;
import org.exalt.bank.domain.port.in.BankAccountStatementUseCase;
import org.exalt.bank.domain.port.out.BankAccountStatementPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class BankAccountStatementService implements BankAccountStatementUseCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountStatementService.class);
    private final BankAccountStatementPort bankAccountStatementPort;

    public BankAccountStatementService(BankAccountStatementPort bankAccountStatementPort) {this.bankAccountStatementPort = bankAccountStatementPort;}

    @Override
    public Optional<BankAccountStatement> retrieveAccountOperations(UUID accountId, LocalDateTime dateOfIssue) {
        try {
            var accountOperations = this.bankAccountStatementPort.retrieveAccountOperations(accountId, dateOfIssue);
            var balanceAtIssueDate = calculateBalanceAtIssueDate(accountOperations);

            return Optional.of(
                    BankAccountStatement.builder()
                            .withAmountAtIssueDate(balanceAtIssueDate)
                            .withAccountOperations(accountOperations)
                            .build()
            );
        } catch (AccountOperationsException e) {
            LOGGER.warn("Failed to retrieve operations for bank account with ID {} for the date {}", accountId, dateOfIssue);
            return Optional.empty();
        }
    }

    private BigDecimal calculateBalanceAtIssueDate(List<AccountOperation> accountOperations) {

        return accountOperations
                .stream()
                .filter(operation -> operation.getOperationDateTime() != null)
                .map(operation -> Objects.equals(operation.getOperationType(), OperationType.DEPOSIT) ? operation.getAmount() : operation.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
