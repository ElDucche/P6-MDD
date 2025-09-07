package com.elducche.mdd.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration OpenAPI/Swagger pour l'application MDD
 * 
 * Cette configuration génère automatiquement la documentation API
 * accessible via /swagger-ui.html et /v3/api-docs
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "MDD API",
        version = "1.0.0",
        description = "API REST pour l'application MDD - Réseau social de développeurs",
        contact = @Contact(
            name = "Équipe MDD",
            email = "contact@mdd.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            description = "Serveur de développement",
            url = "http://localhost:8080"
        ),
        @Server(
            description = "Serveur de production",
            url = "https://api.mdd.com"
        )
    }
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "Token JWT obtenu via l'endpoint de connexion (/api/auth/login)"
)
public class OpenApiConfig {
}
