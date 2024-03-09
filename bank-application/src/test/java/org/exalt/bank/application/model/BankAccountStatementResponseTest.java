package org.exalt.bank.application.model;

import org.exalt.bank.application.enums.OperationTypeResponse;
import org.exalt.bank.domain.enums.OperationType;
import org.exalt.bank.domain.model.AccountOperation;
import org.exalt.bank.domain.model.BankAccountStatement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BankAccountStatementResponseTest {
    private final static UUID OPERATION_ID = UUID.randomUUID();
    private final static LocalDateTime OPERATION_DATE_TIME = LocalDateTime.of(2024, 11, 11, 12, 12, 30);

    @Test
    void should_map_data_from_bank_account_statement() {
        // Act
        var actual = BankAccountStatementResponse.from(buildBankAccountStatement());

        // Assert
        assertThat(actual).isEqualTo(buildBankAccountStatementResponse());
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

    private static AccountOperationResponse buildAccountOperationResponse() {
        return AccountOperationResponse.builder()
                .withOperationId(OPERATION_ID)
                .withOperationDateTime(OPERATION_DATE_TIME)
                .withAmount(new BigDecimal("100"))
                .withAccountType("CURRENT")
                .withOperationType(OperationTypeResponse.WITHDRAWAL)
                .build();
    }

    private static BankAccountStatement buildBankAccountStatement() {
        return BankAccountStatement.builder()
                .withAccountOperations(Collections.singletonList(buildAccountOperation()))
                .withAmountAtIssueDate(new BigDecimal("3000"))
                .build();
    }

    private static BankAccountStatementResponse buildBankAccountStatementResponse() {
        return BankAccountStatementResponse.builder()
                .withAccountOperations(Collections.singletonList(buildAccountOperationResponse()))
                .withAmountAtIssueDate(new BigDecimal("3000"))
                .build();
    }
}