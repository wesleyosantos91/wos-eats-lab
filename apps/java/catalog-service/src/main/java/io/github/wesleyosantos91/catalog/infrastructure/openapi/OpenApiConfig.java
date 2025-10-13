package io.github.wesleyosantos91.catalog.infrastructure.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .security(List.of(securityRequirement()))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme()))
                .tags(apiTags());
    }

    private Info apiInfo() {
        return new Info()
                .title("Catalog API")
                .version("v1.0.0")
                .description("API para gerenciamento de catálogo de restaurantes, cozinhas e produtos do sistema WOS Eats")
                .contact(apiContact())
                .license(apiLicense());
    }

    private Contact apiContact() {
        return new Contact()
                .name("Wesley Oliveira Santos")
                .email("wesleyosantos91@gmail.com")
                .url("https://github.com/wesleyosantos91");
    }

    private License apiLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    private List<Server> apiServers() {
        return List.of(
                new Server()
                        .url("http://localhost:8090")
                        .description("Servidor de Desenvolvimento"),
                new Server()
                        .url("http://localhost:8000")
                        .description("Servidor através do Kong Gateway"),
                new Server()
                        .url("https://api.wos-eats.com")
                        .description("Servidor de Produção")
        );
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("bearerAuth");
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Token JWT para autenticação");
    }

    private List<Tag> apiTags() {
        return List.of(
                new Tag()
                        .name("Kitchens")
                        .description("Operações relacionadas ao gerenciamento de cozinhas"),
                new Tag()
                        .name("Restaurants")
                        .description("Operações relacionadas ao gerenciamento de restaurantes"),
                new Tag()
                        .name("Products")
                        .description("Operações relacionadas ao gerenciamento de produtos"),
                new Tag()
                        .name("Health")
                        .description("Endpoints de verificação de saúde da aplicação")
        );
    }
}
