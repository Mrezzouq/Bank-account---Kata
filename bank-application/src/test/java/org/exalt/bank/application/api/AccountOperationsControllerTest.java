package org.exalt.bank.application.api;

import org.exalt.bank.domain.model.BankAccountOperationResult;
import org.exalt.bank.domain.port.in.BankAccountOperationUseCase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountOperationsControllerTest {
    private static final String ACCOUNT_NOT_FOUND_FAILURE_MESSAGE = "bank account not found";
    private final static UUID ACCOUNT_ID = UUID.randomUUID();
    @Mock
    private BankAccountOperationUseCase bankAccountOperationUseCase;

    @InjectMocks
    private AccountOperationsController accountOperationsController;

    @Nested
    class Withdrawal {
        private final static String WITHDRAWAL_SUCCESS_MESSAGE = "Withdrawal successful";
        @Test
        void should_withdrawal_Success() {
            // Arrange
            var withdrawalAmount = new BigDecimal("100");
            var successResult = new BankAccountOperationResult.Ok(WITHDRAWAL_SUCCESS_MESSAGE);
            when(bankAccountOperationUseCase.withdrawal(ACCOUNT_ID, withdrawalAmount)).thenReturn(successResult);

            //Act
            var actual = accountOperationsController.withdrawal(ACCOUNT_ID, withdrawalAmount);

            // Assert
            var expected = new BankAccountOperationResult.Ok(WITHDRAWAL_SUCCESS_MESSAGE);
            assertThat(actual)
                    .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
                    .contains(HttpStatus.CREATED, expected);
        }

        @ParameterizedTest
        @ValueSource(strings = {"0", "-10", "-100"})
        void should_return_bad_request_when_withdrawal_amount_is_invalid(String stringAmount) {
            //Act
            var actual = accountOperationsController.withdrawal(ACCOUNT_ID, new BigDecimal(stringAmount));

            //Assert
            var expected = new BankAccountOperationResult.Failure("withdrawal amount must be greater than zero.");
            assertThat(actual)
                    .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
                    .contains(HttpStatus.BAD_REQUEST, expected);

        }

        @Test
        void should_return_not_found_when_no_account_is_found() {
            // Arrange
            var withdrawalAmount = new BigDecimal("100");
            var notFoundResult = new BankAccountOperationResult.Failure(ACCOUNT_NOT_FOUND_FAILURE_MESSAGE);
            when(bankAccountOperationUseCase.withdrawal(ACCOUNT_ID, withdrawalAmount)).thenReturn(notFoundResult);

            //Act
            var actual = accountOperationsController.withdrawal(ACCOUNT_ID, withdrawalAmount);

            // Assert
            var expected = new BankAccountOperationResult.Failure(ACCOUNT_NOT_FOUND_FAILURE_MESSAGE);
            assertThat(actual)
                    .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
                    .contains(HttpStatus.NOT_FOUND, expected);
        }

        @Test
        void should_return_internal_server_error_when_withdrawal_fails() {
            // Arrange
            var withdrawalAmount = new BigDecimal("100");
            var failedResult = new BankAccountOperationResult.Failure("operation failed");
            when(bankAccountOperationUseCase.withdrawal(ACCOUNT_ID, withdrawalAmount)).thenReturn(failedResult);

            //Act
            var actual = accountOperationsController.withdrawal(ACCOUNT_ID, withdrawalAmount);

            // Assert
            var expected = new BankAccountOperationResult.Failure("operation failed");
            assertThat(actual)
                    .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
                    .contains(HttpStatus.INTERNAL_SERVER_ERROR, expected);
        }
    }

    @Nested
    class Deposit {
        private static final String OPERATION_FAILED_EXCEPTION_MESSAGE = "Operation failed due to a bank operation error";
        private final static String DEPOSIT_SUCCESS_MESSAGE = "Deposit successful";

        @Test
        void should_deposit_Success() {
            // Arrange
            var depositAmount = new BigDecimal("100");
            var successResult = new BankAccountOperationResult.Ok(DEPOSIT_SUCCESS_MESSAGE);
            when(bankAccountOperationUseCase.deposit(ACCOUNT_ID, depositAmount)).thenReturn(successResult);

            //Act
            var actual = accountOperationsController.deposit(ACCOUNT_ID, depositAmount);

            // Assert
            var expected = new BankAccountOperationResult.Ok(DEPOSIT_SUCCESS_MESSAGE);
            assertThat(actual)
                    .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
                    .contains(HttpStatus.CREATED, expected);
        }

        @ParameterizedTest
        @ValueSource(strings = {"0", "-10", "-100"})
        void should_return_bad_request_when_deposit_amount_is_invalid(String stringAmount) {
            //Act
            var actual = accountOperationsController.deposit(ACCOUNT_ID, new BigDecimal(stringAmount));

            //Assert
            var expected = new BankAccountOperationResult.Failure("Deposit amount must be greater than zero.");
            assertThat(actual)
                    .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
                    .contains(HttpStatus.BAD_REQUEST, expected);

        }

        @Test
        void should_return_not_found_when_no_account_is_found() {
            // Arrange
            var depositAmount = new BigDecimal("100");
            var notFoundResult = new BankAccountOperationResult.Failure(ACCOUNT_NOT_FOUND_FAILURE_MESSAGE);
            when(bankAccountOperationUseCase.deposit(ACCOUNT_ID, depositAmount)).thenReturn(notFoundResult);

            //Act
            var actual = accountOperationsController.deposit(ACCOUNT_ID, depositAmount);

            // Assert
            var expected = new BankAccountOperationResult.Failure(ACCOUNT_NOT_FOUND_FAILURE_MESSAGE);
            assertThat(actual)
                    .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
                    .contains(HttpStatus.NOT_FOUND, expected);
        }

        @Test
        void should_return_internal_server_error_when_deposit_fails() {
            // Arrange
            var depositAmount = new BigDecimal("100");
            var failureResult = new BankAccountOperationResult.Failure(OPERATION_FAILED_EXCEPTION_MESSAGE);
            when(bankAccountOperationUseCase.deposit(ACCOUNT_ID, depositAmount)).thenReturn(failureResult);

            //Act
            var actual = accountOperationsController.deposit(ACCOUNT_ID, depositAmount);

            // Assert
            var expected = new BankAccountOperationResult.Failure(OPERATION_FAILED_EXCEPTION_MESSAGE);
            assertThat(actual)
                    .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
                    .contains(HttpStatus.INTERNAL_SERVER_ERROR, expected);
        }
    }
}