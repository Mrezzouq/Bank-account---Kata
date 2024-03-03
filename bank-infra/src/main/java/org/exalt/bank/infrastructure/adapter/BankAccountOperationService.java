package org.exalt.bank.infrastructure.adapter;

import org.exalt.bank.domain.exceptions.BankAccountNotFoundException;
import org.exalt.bank.domain.exceptions.BankOperationsException;
import org.exalt.bank.domain.model.BankAccountOperationResult;
import org.exalt.bank.domain.port.out.BankAccountOperationPort;
import org.exalt.bank.infrastructure.entities.AccountOperationEntity;
import org.exalt.bank.infrastructure.entities.BankAccountEntity;
import org.exalt.bank.infrastructure.entities.CurrentAccountEntity;
import org.exalt.bank.infrastructure.entities.SavingAccountEntity;
import org.exalt.bank.infrastructure.enums.OperationType;
import org.exalt.bank.infrastructure.repositories.AccountOperationRepository;
import org.exalt.bank.infrastructure.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@Transactional
public class BankAccountOperationService implements BankAccountOperationPort {
    public static final String INSUFFICIENT_FUNDS_EXCEPTION_MESSAGE = "The withdrawal amount exceeds the available balance and the authorized overdraft limit.";
    public static final String DEPOSIT_EXCEEDS_LIMIT_EXCEPTION_MESSAGE = "Deposit exceeds the maximum allowed deposit limit for SavingAccount.";

    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;

    @Autowired
    public BankAccountOperationService(BankAccountRepository bankAccountRepository, AccountOperationRepository accountOperationRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.accountOperationRepository = accountOperationRepository;
    }

    @Override
    public BankAccountOperationResult withdrawal(UUID accountId, BigDecimal withdrawalAmount) {
        var account = findAccountById(accountId);
        BigDecimal overDraftLimit = account instanceof CurrentAccountEntity currentAccount ? currentAccount.getOverdraftLimit() : BigDecimal.ZERO;
        if (!canWithdraw(account.getBalance(), withdrawalAmount, overDraftLimit)) {
            return new BankAccountOperationResult.Failure(INSUFFICIENT_FUNDS_EXCEPTION_MESSAGE);
        }
        return performTransaction(account, withdrawalAmount.negate(), OperationType.WITHDRAWAL);
    }

    @Override
    public BankAccountOperationResult deposit(UUID accountId, BigDecimal amount) {
        var account = findAccountById(accountId);
        if (account instanceof SavingAccountEntity savingAccount && exceedsDepositLimit(savingAccount, amount)) {
            throw new BankOperationsException(DEPOSIT_EXCEEDS_LIMIT_EXCEPTION_MESSAGE);
        }
        return performTransaction(account, amount, OperationType.DEPOSIT);
    }

    private BankAccountEntity findAccountById(UUID accountId) {
        return bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));
    }

    private boolean canWithdraw(BigDecimal currentBalance, BigDecimal withdrawalAmount, BigDecimal overDraftLimit) {
        return currentBalance.subtract(withdrawalAmount).compareTo(overDraftLimit.negate()) >= 0;
    }

    private boolean exceedsDepositLimit(SavingAccountEntity account, BigDecimal amount) {
        var newBalance = account.getBalance().add(amount);
        return newBalance.compareTo(account.getDepositLimit()) > 0;
    }

    private BankAccountOperationResult performTransaction(BankAccountEntity account, BigDecimal amount, OperationType operationType) {
        AccountOperationEntity operation = createAccountOperation(account, amount, operationType);
        accountOperationRepository.save(operation);
        updateAccountBalance(account, amount);
        return new BankAccountOperationResult.Ok(operationType + " successful");
    }

    private AccountOperationEntity createAccountOperation(BankAccountEntity account, BigDecimal amount, OperationType operationType) {
        return AccountOperationEntity.builder()
                .withOperationId(UUID.randomUUID())
                .withOperationDateTime(LocalDateTime.now())
                .withAmount(amount)
                .withOperationType(operationType)
                .withAccountType(account instanceof SavingAccountEntity ? "Saving" : "Current")
                .withBankAccount(account)
                .build();
    }

    private void updateAccountBalance(BankAccountEntity account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        bankAccountRepository.save(account);
    }
}
