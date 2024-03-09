package org.exalt.bank.application.api;

import org.exalt.bank.application.model.BankAccountStatementResponse;
import org.exalt.bank.domain.enums.OperationType;
import org.exalt.bank.domain.model.AccountOperation;
import org.exalt.bank.domain.model.BankAccountStatement;
import org.exalt.bank.domain.port.in.BankAccountStatementUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountStatementControllerTest {
    private final static UUID OPERATION_ID = UUID.randomUUID();
    private final static LocalDateTime OPERATION_DATE_TIME = LocalDateTime.of(2024, 11, 11, 12, 12, 30);
    @Mock
    private BankAccountStatementUseCase bankAccountStatementUseCase;
    @InjectMocks
    private AccountStatementController accountStatementController;

    @Test
    void should_return_account_operations_successfully() {
        // Arrange
        var accountId = UUID.randomUUID();
        var date = "2023-01-01T00:00:00";
        when(bankAccountStatementUseCase.retrieveAccountOperations(accountId, LocalDateTime.parse(date)))
                .thenReturn(Optional.of(buildBankAccountStatement()));

        // Act
        var actual = accountStatementController.retrieveAccountStatement(accountId, date);

        // Assert
        var expected = BankAccountStatementResponse.from(buildBankAccountStatement());
        assertThat(actual)
                .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
                .contains(HttpStatus.OK, expected);
    }

    @Test
    void should_return_ok_with_empty_body_when_failed_to_retrieve_account_statement() {
        // Arrange
        var accountId = UUID.randomUUID();
        var date = "2023-01-01T00:00:00";
        when(bankAccountStatementUseCase.retrieveAccountOperations(accountId, LocalDateTime.parse(date))).thenReturn(Optional.empty());

        // Act
        var actual = accountStatementController.retrieveAccountStatement(accountId, date);

        // Assert
        assertThat(actual)
                .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
                .contains(HttpStatus.OK, BankAccountStatementResponse.EMPTY);

    }

    @Test
    void should_return_bad_request_given_invalid_date() {
        //Arrange
        var accountId = UUID.randomUUID();
        var invalidDate = "invalid-date";

        // Act
        var actual = accountStatementController.retrieveAccountStatement(accountId, invalidDate);

        // Assert
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private static AccountOperation buildAccountOperation() {
        return AccountOperation.builder()
                .withOperationId(OPERATION_ID)
                .withOperationDateTime(OPERATION_DATE_TIME)
                .withAmount(new BigDecimal("100"))
                .withAccountType("CURRENT")
                .withOperationType(OperationType.WITHDRAWAL)
                .build();
    }

    private static BankAccountStatement buildBankAccountStatement() {
        return BankAccountStatement.builder()
                .withAccountOperations(Collections.singletonList(buildAccountOperation()))
                .withAmountAtIssueDate(new BigDecimal("3000"))
                .build();
    }
}