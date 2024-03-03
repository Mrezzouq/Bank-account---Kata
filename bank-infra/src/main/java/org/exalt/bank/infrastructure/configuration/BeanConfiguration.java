package org.exalt.bank.infrastructure.configuration;

import org.exalt.bank.domain.port.in.BankAccountOperationUseCase;
import org.exalt.bank.domain.port.in.BankAccountStatementUseCase;
import org.exalt.bank.domain.port.out.BankAccountOperationPort;
import org.exalt.bank.domain.port.out.BankAccountStatementPort;
import org.exalt.bank.domain.usecases.BankAccountService;
import org.exalt.bank.domain.usecases.BankAccountStatementService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public BankAccountOperationUseCase bankAccountOperationUseCase(BankAccountOperationPort bankAccountOperationPort) {
        return new BankAccountService(bankAccountOperationPort);
    }

    @Bean
    public BankAccountStatementUseCase bankAccountStatementUseCase(BankAccountStatementPort bankAccountStatementPort) {
        return new BankAccountStatementService(bankAccountStatementPort);
    }
}
