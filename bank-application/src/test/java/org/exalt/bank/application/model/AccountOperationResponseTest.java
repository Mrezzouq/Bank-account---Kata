package org.exalt.bank.application.model;

import org.exalt.bank.application.enums.OperationTypeResponse;
import org.exalt.bank.domain.enums.OperationType;
import org.exalt.bank.domain.model.AccountOperation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AccountOperationResponseTest {
    private final static UUID OPERATION_ID = UUID.randomUUID();
    private final static LocalDateTime OPERATION_DATE_TIME = LocalDateTime.of(2024,11,11,12,12,30);

    @Test
    void should_map_data_from_account_operation () {
        // Act
        var actual =AccountOperationResponse.from(buildAccountOperation());

        // Assert
        assertThat(actual).isEqualTo(buildAccountOperationResponse());
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
}