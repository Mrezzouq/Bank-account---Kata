package org.exalt.bank.domain.usecases;

import org.exalt.bank.domain.enums.OperationType;
import org.exalt.bank.domain.exceptions.AccountOperationsException;
import org.exalt.bank.domain.model.AccountOperation;
import org.exalt.bank.domain.model.BankAccount;
import org.exalt.bank.domain.model.BankAccountStatement;
import org.exalt.bank.domain.port.out.BankAccountStatementPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountStatementServiceTest {
    private static final LocalDateTime OPERATION_DATE_TIME = LocalDateTime.now();
    @Mock
    private BankAccountStatementPort bankAccountStatementPort;
    @Mock
    private BankAccount bankAccount;
    @InjectMocks
    private BankAccountStatementService bankAccountStatementService;

    private static AccountOperation buildAccountOperation(String accountType, OperationType operationType, LocalDateTime operationDateTime) {
        return AccountOperation.builder()
                .withOperationId(UUID.randomUUID())
                .withAmount(new BigDecimal("300"))
                .withAccountType(accountType)
                .withOperationDateTime(operationDateTime)
                .withOperationType(operationType)
                .build();
    }

    @Test
    void should_return_empty_list_when_account_has_no_operations() {
        // Arrange
        when(bankAccountStatementPort.retrieveAccountOperations(any(), any())).thenThrow(new AccountOperationsException("no operations found"));

        // Act
        var actual = bankAccountStatementService.retrieveAccountOperations(any(), any());

        // Assert
        assertThat(actual).isEmpty();
    }

    @Test
    void should_return_operations_when_bank_account_has_operations() {
        // Arrange
        var operationDate = LocalDateTime.now().minusDays(15);
        var buildFirstSavingOperation = buildAccountOperation("SAVING", OperationType.DEPOSIT, operationDate);
        var buildSecondSavingOperation = buildAccountOperation("SAVING", OperationType.WITHDRAWAL, operationDate);
        var buildFirstCurrentOperation = buildAccountOperation("CURRENT", OperationType.DEPOSIT, operationDate);
        var buildSecondCurrentOperation = buildAccountOperation("CURRENT", OperationType.WITHDRAWAL, operationDate);
        var accountOperations = List.of(buildFirstSavingOperation, buildSecondSavingOperation, buildFirstCurrentOperation, buildSecondCurrentOperation);
        when(bankAccountStatementPort.retrieveAccountOperations(any(), any())).thenReturn(accountOperations);

        // Act
        var actual = bankAccountStatementService.retrieveAccountOperations(any(), any());

        // Assert
        var expected = BankAccountStatement.builder().withAccountOperations(accountOperations)
                .withAmountAtIssueDate(BigDecimal.ZERO)
                .build();
        assertThat(actual).isEqualTo(Optional.of(expected));
    }
}