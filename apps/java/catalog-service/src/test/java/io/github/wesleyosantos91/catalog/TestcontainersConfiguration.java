package io.github.wesleyosantos91.catalog;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    private static PostgreSQLContainer<?> container;

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.6"))
                .withDatabaseName("postgres")
                .withUsername("postgres")
                .withPassword("postgres");

        return container;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
                String.format("jdbc:postgresql://%s:%d/postgres?currentSchema=catalog_schema",
                        container.getHost(), container.getMappedPort(5432)));
        registry.add("spring.datasource.username", () -> container.getUsername());
        registry.add("spring.datasource.password", () -> container.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.schemas", () -> "catalog_schema");
        registry.add("spring.flyway.enabled", () -> "true");
        // Usar apenas as migrações do test-resources para evitar conflito
        registry.add("spring.flyway.locations", () -> "classpath:/db/migration");
        registry.add("spring.flyway.url", () -> String.format("jdbc:postgresql://%s:%d/postgres?currentSchema=catalog_schema",
                container.getHost(), container.getMappedPort(5432)));
        registry.add("spring.flyway.user", () -> container.getUsername());
        registry.add("spring.flyway.password", () -> container.getPassword());
        registry.add("spring.flyway.clean-disabled", () -> "false");
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
        registry.add("spring.flyway.validate-on-migrate", () -> "false");
    }
}