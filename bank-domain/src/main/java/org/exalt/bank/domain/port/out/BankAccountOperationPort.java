package org.exalt.bank.domain.port.out;

import org.exalt.bank.domain.model.BankAccountOperationResult;

import java.math.BigDecimal;
import java.util.UUID;

public interface BankAccountOperationPort {
    BankAccountOperationResult withdrawal(UUID accountId, BigDecimal amount);

    BankAccountOperationResult deposit(UUID accountId, BigDecimal amount);
}
