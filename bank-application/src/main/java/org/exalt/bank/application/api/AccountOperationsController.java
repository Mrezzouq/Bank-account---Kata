package org.exalt.bank.application.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.exalt.bank.domain.model.BankAccountOperationResult;
import org.exalt.bank.domain.port.in.BankAccountOperationUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("v1/bank/account")
@Tag(name = "Bank Account Operations", description = "gestion des dépots et retraits des comptes bancaires")
public class AccountOperationsController {
    static final String ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE = "bank account not found";
    private final BankAccountOperationUseCase bankAccountOperationUseCase;

    public AccountOperationsController(BankAccountOperationUseCase bankAccountOperationUseCase) {
        this.bankAccountOperationUseCase = bankAccountOperationUseCase;
    }

    @PostMapping(value = "/{accountId}/withdrawal", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Effectue un retrait d'un compte bancaire",
            description = "Retire un montant spécifié du solde d'un compte bancaire.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Retrait effectué avec succès",
                            content = @Content(schema = @Schema(implementation = BankAccountOperationResult.class))),
                    @ApiResponse(responseCode = "400", description = "Requête invalide"),
                    @ApiResponse(responseCode = "500", description = "retrait echoués")
            })
    public ResponseEntity<BankAccountOperationResult> withdrawal(
            @PathVariable @NotBlank @Parameter(description = "Identifiant unique du compte") UUID accountId,
            @RequestBody @NotBlank @Parameter(description = "Montant à retirer") BigDecimal withdrawalAmount) {
        if (BigDecimal.ZERO.compareTo(withdrawalAmount) >= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BankAccountOperationResult.Failure("withdrawal amount must be greater than zero."));
        }

        var result = bankAccountOperationUseCase.withdrawal(accountId, withdrawalAmount);
        return result instanceof BankAccountOperationResult.Failure failure
                ? handleFailure(failure)
                : ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping(value = "/{accountId}/deposit", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Effectue un dépôt sur un compte bancaire",
            description = "Ajoute un montant spécifié au solde d'un compte bancaire.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Dépôt effectué avec succès",
                            content = @Content(schema = @Schema(implementation = BankAccountOperationResult.class))),
                    @ApiResponse(responseCode = "400", description = "paramètre invalide"),
                    @ApiResponse(responseCode = "404", description = "Compte non trouvé"),
                    @ApiResponse(responseCode = "500", description = "dêpot echoué")
            })
    public ResponseEntity<BankAccountOperationResult> deposit(
            @PathVariable @NotBlank @Parameter(description = "Identifiant unique du compte") UUID accountId,
            @RequestBody @NotNull @Parameter(description = "Montant à déposer") BigDecimal depositAmount) {

        if (BigDecimal.ZERO.compareTo(depositAmount) >= 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BankAccountOperationResult.Failure("Deposit amount must be greater than zero."));
        var result = this.bankAccountOperationUseCase.deposit(accountId, depositAmount);
        if (result instanceof BankAccountOperationResult.Failure failure) {
            return handleFailure(failure);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    private ResponseEntity<BankAccountOperationResult> handleFailure(BankAccountOperationResult.Failure failure) {
        HttpStatus status;
        if (failure.getOperationMessage().equals(ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE)) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(status).body(failure);
    }
}
