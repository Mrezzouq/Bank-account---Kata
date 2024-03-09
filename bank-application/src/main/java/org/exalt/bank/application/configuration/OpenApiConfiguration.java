package org.exalt.bank.application.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Bank account EXALTIT kata",
                version = "v1.0",
                description = "Cette API fournit des services pour gérer un système bancaire. Elle permet des opérations sur les comptes bancaires, y compris la gestion des dépôts, des retraits et la fourniture de relevés mensuels."
        ),
        servers = @Server(
                description = "dev",
                url = "http://localhost:8090"
        )
)
public class OpenApiConfiguration {

}
