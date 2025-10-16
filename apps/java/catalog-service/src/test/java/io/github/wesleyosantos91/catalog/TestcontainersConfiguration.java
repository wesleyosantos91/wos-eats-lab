package io.github.wesleyosantos91.catalog;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    private static PostgreSQLContainer<?> postgreSQLContainer;
    private static RedisContainer redisContainer;

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.6"))
                .withDatabaseName("postgres")
                .withUsername("postgres")
                .withPassword("postgres");

        return postgreSQLContainer;
    }

    @Bean
    @ServiceConnection
    RedisContainer redisContainer() {
        redisContainer = new RedisContainer(DockerImageName.parse("redis:7.4.1-alpine"))
                .withExposedPorts(6379);
        return redisContainer;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL configuration
        registry.add("spring.datasource.url", () ->
                String.format("jdbc:postgresql://%s:%d/postgres?currentSchema=catalog_schema",
                        postgreSQLContainer.getHost(), postgreSQLContainer.getMappedPort(5432)));
        registry.add("spring.datasource.username", () -> postgreSQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> postgreSQLContainer.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.schemas", () -> "catalog_schema");
        registry.add("spring.flyway.enabled", () -> "true");
        // Usar apenas as migrações do test-resources para evitar conflito
        registry.add("spring.flyway.locations", () -> "classpath:/db/migration");
        registry.add("spring.flyway.url", () -> String.format("jdbc:postgresql://%s:%d/postgres?currentSchema=catalog_schema",
                postgreSQLContainer.getHost(), postgreSQLContainer.getMappedPort(5432)));
        registry.add("spring.flyway.user", () -> postgreSQLContainer.getUsername());
        registry.add("spring.flyway.password", () -> postgreSQLContainer.getPassword());
        registry.add("spring.flyway.clean-disabled", () -> "false");
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
        registry.add("spring.flyway.validate-on-migrate", () -> "false");
        
        // Redis configuration
        registry.add("spring.data.redis.host", () -> redisContainer.getHost());
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }
}