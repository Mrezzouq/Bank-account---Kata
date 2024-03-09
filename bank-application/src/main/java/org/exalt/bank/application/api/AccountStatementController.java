package org.exalt.bank.application.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.exalt.bank.application.model.BankAccountStatementResponse;
import org.exalt.bank.domain.port.in.BankAccountStatementUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@RestController
@RequestMapping("v1/bank/statement")
@Validated
@Tag(name = "Bank Account statement", description = "récupération d'état d'un compte sur un mois glissant")
public class AccountStatementController {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final BankAccountStatementUseCase bankAccountStatementUseCase;

    public AccountStatementController(BankAccountStatementUseCase bankAccountStatementUseCase) {
        this.bankAccountStatementUseCase = bankAccountStatementUseCase;
    }

    @GetMapping(value = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupération des opérations d'un compte",
            description = "Cet API permet de récupérer la liste des opérations sur un mois glissant",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = BankAccountStatementResponse.class))),
                    @ApiResponse(responseCode = "400", description = "date incorrecte")
            }
    )
    public ResponseEntity<BankAccountStatementResponse> retrieveAccountStatement(
            @PathVariable @NotBlank @Parameter(description = "Identifiant unique du compte") UUID accountId,
            @RequestBody @NotBlank @Parameter(description = "Date de début pour le relevé des opérations") String date
    ) {
        LocalDateTime dateOfIssue = null;
        try {
            dateOfIssue = LocalDateTime.parse(date, FORMATTER);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }

        var result = bankAccountStatementUseCase.retrieveAccountOperations(accountId, dateOfIssue);
        return result.map(bankAccountStatement -> ResponseEntity.ok(BankAccountStatementResponse.from(bankAccountStatement)))
                .orElseGet(() -> ResponseEntity.ok().body(BankAccountStatementResponse.EMPTY));

    }
}
