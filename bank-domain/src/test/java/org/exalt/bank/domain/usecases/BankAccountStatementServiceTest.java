package org.exalt.bank.domain.usecases;

import org.exalt.bank.domain.enums.OperationType;
import org.exalt.bank.domain.model.AccountOperation;
import org.exalt.bank.domain.model.BankAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountStatementServiceTest {

    @Mock
    BankAccount bankAccount;
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
        when(bankAccount.getAccountOperations()).thenReturn(Collections.emptyList());

        // Act
        var actual = bankAccountStatementService.retrieveAccountOperations(bankAccount, LocalDateTime.now());

        // Assert
        assertThat(actual.getAccountOperations()).isEmpty();
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
        when(bankAccount.getAccountOperations()).thenReturn(accountOperations);

        // Act
        var actual = bankAccountStatementService.retrieveAccountOperations(bankAccount, LocalDateTime.now());

        // Assert
        assertThat(actual.getAccountOperations()).containsExactlyInAnyOrderElementsOf(accountOperations);
    }

    @Test
    void should_return_empty_list_when_operations_are_outside_date_range() {
        // Arrange
        var operationDate = LocalDateTime.now().minusDays(10);
        var buildFirstSavingOperation = buildAccountOperation("SAVING", OperationType.DEPOSIT, operationDate);
        var buildSecondCurrentOperation = buildAccountOperation("CURRENT", OperationType.WITHDRAWAL, operationDate);
        var accountOperations = List.of(buildFirstSavingOperation, buildSecondCurrentOperation);
        when(bankAccount.getAccountOperations()).thenReturn(accountOperations);

        // Act
        var actual = bankAccountStatementService.retrieveAccountOperations(bankAccount, LocalDateTime.now().plusDays(100));

        // Assert
        assertThat(actual.getAccountOperations()).isEmpty();
    }

    @Test
    void should_correctly_sort_operations_in_descending_order() {
        // Arrange
        var buildFirstSavingOperation = buildAccountOperation("SAVING", OperationType.DEPOSIT, LocalDateTime.now().minusDays(13));
        var buildSecondCurrentOperation = buildAccountOperation("CURRENT", OperationType.WITHDRAWAL, LocalDateTime.now().minusDays(12));
        var accountOperations = List.of(buildFirstSavingOperation, buildSecondCurrentOperation);
        when(bankAccount.getAccountOperations()).thenReturn(accountOperations);

        // Act
        var actual = bankAccountStatementService.retrieveAccountOperations(bankAccount, LocalDateTime.now());

        //Assert
        assertThat(actual.getAccountOperations().get(0)).isEqualTo(buildSecondCurrentOperation);
        assertThat(actual.getAccountOperations().get(1)).isEqualTo(buildFirstSavingOperation);
    }
}