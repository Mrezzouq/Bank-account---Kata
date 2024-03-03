package org.exalt.bank.infrastructure.adapter;

import org.assertj.core.api.Assertions;
import org.exalt.bank.domain.exceptions.AccountOperationsException;
import org.exalt.bank.infrastructure.entities.AccountOperationEntity;
import org.exalt.bank.infrastructure.repositories.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountStatementRetrieverTest {
    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountStatementRetriever bankAccountStatementRetriever;
    private UUID accountId;
    private LocalDateTime dateOfIssue;
    private LocalDateTime startDate;
    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        dateOfIssue = LocalDateTime.now();
        startDate = dateOfIssue.minusDays(31);
    }

    @Test
    void should_return_list_of_operation_when_found() {
        // Arrange
        var mockOperations = List.of(mock(AccountOperationEntity.class));
        when(bankAccountRepository.findOperationsByAccountId(accountId, startDate, dateOfIssue)).thenReturn(mockOperations);

        // Act
        var actual = bankAccountStatementRetriever.retrieveAccountOperations(accountId, dateOfIssue);

        // Assert
        assertThat(actual).isNotEmpty();
    }

    @Test
    void should_throw_BankAccountOperationsException_when_no_operations_are_found() {
        // Arrange
        when(bankAccountRepository.findOperationsByAccountId(accountId, startDate, dateOfIssue)).thenReturn(Collections.emptyList());

        // Act
        ThrowingCallable actual = () -> bankAccountStatementRetriever.retrieveAccountOperations(accountId, dateOfIssue);

        // Assert
        Assertions.assertThatThrownBy(actual).isInstanceOf(AccountOperationsException.class)
                .hasMessage("No operations found for the bank account with ID: " + accountId + " within the date range: " + startDate + " to " + dateOfIssue + ".");
    }
}