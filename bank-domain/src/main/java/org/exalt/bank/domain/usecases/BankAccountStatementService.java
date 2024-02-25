package org.exalt.bank.domain.usecases;

import org.exalt.bank.domain.enums.OperationType;
import org.exalt.bank.domain.model.AccountOperation;
import org.exalt.bank.domain.model.BankAccount;
import org.exalt.bank.domain.model.BankAccountStatement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class BankAccountStatementService {

    public BankAccountStatement retrieveAccountOperations(BankAccount account, LocalDateTime dateOfIssue) {
        Objects.requireNonNull(account, "The account must not be null.");
        Objects.requireNonNull(dateOfIssue, "The issue date must not be null.");

        var balanceAtIssueDate = calculateBalanceAtIssueDate(account, dateOfIssue);

        var accountOperations = Optional.ofNullable(account.getAccountOperations())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(operation -> Optional.ofNullable(operation.getOperationDateTime())
                        .map(dateTime -> dateTime.isAfter(dateOfIssue.minusDays(31)) && dateTime.isBefore(dateOfIssue.plusDays(1)))
                        .orElse(false))
                .sorted(Comparator.comparing(AccountOperation::getOperationDateTime).reversed())
                .toList();

        return BankAccountStatement.builder()
                .withAmountAtIssueDate(balanceAtIssueDate)
                .withAccountOperations(accountOperations)
                .build();
    }

    private BigDecimal calculateBalanceAtIssueDate(BankAccount account, LocalDateTime dateOfIssue) {

        return account.getAccountOperations()
                .stream()
                .filter(operation -> operation.getOperationDateTime() != null && !operation.getOperationDateTime().isAfter(dateOfIssue))
                .map(operation -> Objects.equals(operation.getOperationType(), OperationType.DEPOSIT) ? operation.getAmount() : operation.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
