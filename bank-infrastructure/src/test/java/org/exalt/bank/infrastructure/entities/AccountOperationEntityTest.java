package org.exalt.bank.infrastructure.entities;

import org.exalt.bank.domain.model.AccountOperation;
import org.exalt.bank.infrastructure.enums.OperationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AccountOperationEntityTest {
    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final LocalDateTime OPERATION_DATE_TIME = LocalDateTime.now();

    private static AccountOperationEntity buildAccountOperationEntity() {
        return AccountOperationEntity.builder()
                .withOperationId(ACCOUNT_ID)
                .withAmount(new BigDecimal("2000"))
                .withAccountType("Saving")
                .withOperationType(OperationType.WITHDRAWAL)
                .withOperationDateTime(OPERATION_DATE_TIME)
                .build();
    }

    private static AccountOperation buildAccountOperation() {
        return AccountOperation.builder()
                .withOperationId(ACCOUNT_ID)
                .withAmount(new BigDecimal("2000"))
                .withAccountType("Saving")
                .withOperationType(org.exalt.bank.domain.enums.OperationType.WITHDRAWAL)
                .withOperationDateTime(OPERATION_DATE_TIME)
                .build();
    }

    @Test
    void should_map_to_domain_account_operation() {
        var actual = buildAccountOperationEntity().toAccountOperation();

        assertThat(actual).isEqualTo(buildAccountOperation());
    }
}