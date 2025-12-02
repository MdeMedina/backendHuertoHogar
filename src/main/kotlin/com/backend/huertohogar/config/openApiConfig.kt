package com.backend.huertohogar.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "API HuertoHogar",
        version = "1.0",
        description = "Documentación backend para el caso HuertoHogar - Evaluación 3"
    )
)
@SecurityScheme(
    name = "bearerAuth", // Este nombre se usa para referenciar la seguridad en los controladores
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
class OpenApiConfig