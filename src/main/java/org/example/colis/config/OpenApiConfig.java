package org.example.colis.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Système de Gestion de Colis et Transporteurs API")
                        .version("1.0")
                        .description("API REST pour la gestion de colis avec différents types (STANDARD, FRAGILE, FRIGO) " +
                                "et gestion des transporteurs avec spécialités. Authentification JWT stateless."))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Entrez le JWT token obtenu lors de l'authentification")));
    }
}
