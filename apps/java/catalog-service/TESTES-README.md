# 🧪 Guia de Testes - Catalog Service

## 📋 Visão Geral

Este projeto implementa três tipos de testes:
- **Unit Tests (UT)**: Testes unitários com JUnit 5 + Mockito
- **Integration Tests (IT)**: Testes de integração com Spring Boot Test + Testcontainers
- **BDD/Regression Tests**: Testes BDD com Cucumber 7 + JUnit Platform

## 🛠️ Stack de Testes

- **JUnit 5 Platform**
- **Cucumber 7.22.2**
- **Spring Boot Test 3.5.6**
- **Testcontainers** (PostgreSQL 17.6)
- **JaCoCo 0.8.14** (cobertura de código - mínimo 90%)
- **PIT Mutation Testing 1.21.0** (mutation threshold: 90%)
- **Mockito** (incluído no spring-boot-starter-test)
- **MockMvc** (testes de controller)

## 📁 Estrutura de Testes

```
src/test/
├── java/
│   └── io/github/wesleyosantos91/catalog/
│       ├── TestcontainersConfiguration.java        # ✅ Configuração Testcontainers (ÚNICA)
│       ├── api/v1/controller/
│       │   ├── KitchenControllerTest.java          # UT - Testes unitários do controller
│       │   └── KitchenControllerIT.java            # IT - Testes de integração
│       ├── domain/service/
│       │   └── KitchenServiceTest.java             # UT - Testes unitários do service
│       └── cucumber/
│           ├── CucumberIT.java                      # Suite BDD (roda no Failsafe)
│           ├── step/
│           │   └── KitchenStep.java                 # Step Definitions
│           └── utils/
│               └── FeatureUtils.java                # Utilitários
└── resources/
    ├── application-integration.yml                  # Configuração profile integration
    ├── application-test.yml                         # Configuração profile test
    └── features/
        └── kitchen.feature                          # Cenários BDD em português
```

## 🐳 Testcontainers Configuration

A classe `TestcontainersConfiguration` é usada por **TODOS** os testes de integração e BDD:

```java
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
    
    @Bean
    @ServiceConnection  // ✨ Spring Boot 3.x auto-configuration
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.6"))
                .withDatabaseName("postgres")
                .withUsername("postgres")
                .withPassword("postgres");
    }
}
```

**Como usar:**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)  // ✅ Importa a configuração
@ActiveProfiles("integration")
class MeuTesteIT { ... }
```

## 🎯 Comandos Maven

### Executar TODOS os testes (UT + IT + BDD)
```bash
mvn clean verify
```

### Executar SOMENTE Unit Tests (Surefire)
```bash
mvn clean test -Dskip.it=true
```
✅ **Resultado atual: 25 testes passando (0 falhas)**

### Executar SOMENTE Integration Tests (Failsafe + BDD)
```bash
mvn clean verify -Dskip.ut=true
```
✅ **Resultado atual: 18 testes executados (0 falhas, 18 passando)**

**Status dos testes:**
- ✅ Todos os testes de integração estão passando
- ✅ Todos os testes BDD/Cucumber estão passando
- ✅ Correções aplicadas com sucesso

### Executar testes de mutação (PIT)
```bash
mvn clean test pitest:mutationCoverage
```

### Pular todos os testes
```bash
mvn clean install -DskipTests
```

ou

```bash
mvn clean install -Dskip.ut=true -Dskip.it=true
```

## 🏷️ Filtrar Testes BDD por Tags

### Executar somente testes @smoke
```bash
mvn verify -Dcucumber.filter.tags="@smoke"
```

### Executar somente testes @regression
```bash
mvn verify -Dcucumber.filter.tags="@regression"
```

### Excluir testes @wip (work in progress)
```bash
mvn verify -Dcucumber.filter.tags="not @wip"
```

## 📊 Resultados dos Testes

### Unit Tests (Surefire)
- **Total**: 25 testes
- **Sucesso**: 25 ✅
- **Falhas**: 0
- **Tempo**: ~6 segundos

### Integration Tests (Failsafe)
- **Total**: 18 testes (10 IT + 8 BDD)
- **Sucesso**: 18 ✅
- **Falhas**: 0 ✅
- **Tempo**: ~18 segundos

#### Testes de Integração (KitchenControllerIT)
- ✅ DELETE /v1/kitchens/{id} - Deletar cozinha (2 testes)
- ✅ PUT /v1/kitchens/{id} - Atualizar cozinha (2 testes)
- ✅ GET /v1/kitchens - Listar cozinhas (2 testes)
- ✅ GET /v1/kitchens/{id} - Buscar cozinha por ID (2 testes)
- ✅ POST /v1/kitchens - Criar cozinha (3 testes)

#### Testes BDD/Cucumber
- ✅ Criar uma nova cozinha com sucesso
- ✅ Buscar uma cozinha por ID
- ✅ Listar cozinhas com paginação
- ✅ Atualizar uma cozinha existente
- ✅ Deletar uma cozinha
- ✅ Tentar criar cozinha com nome duplicado
- ✅ Buscar cozinha inexistente

## 📈 Cobertura de Código (JaCoCo)

### Configuração
- **Mínimo de cobertura**: 90% (LINE coverage)
- **Exclusões**:
  - Application.class
  - Entidades (entity/*)
  - DTOs (request/*, response/*)
  - Exceções (exception/*)
  - Mappers (mapper/*)
  - Métricas (metrics/*)

### Executar relatório de cobertura
```bash
mvn clean verify
```

Relatório gerado em: `target/site/jacoco/index.html`

## 🧬 Testes de Mutação (PIT)

### Configuração
- **Mutation Threshold**: 90%
- **Plugin**: pitest-junit5-plugin 1.2.3

### Executar
```bash
mvn clean test pitest:mutationCoverage
```

Relatório gerado em: `target/pit-reports/index.html`

## 🔍 Checkstyle

### Executar validação
```bash
mvn checkstyle:check
```

### Configuração
- Arquivo: `checkstyle.xml`
- Modo: `failOnViolation=true`
- Resultado atual: ✅ 0 violações

## 📝 Boas Práticas

### 1. Nomenclatura de Testes
- **Unit Tests**: `*Test.java` (executados pelo Surefire)
- **Integration Tests**: `*IT.java` (executados pelo Failsafe)
- Usar padrão Given-When-Then nos testes BDD
- Usar `@DisplayName` para descrever cenários em português

### 2. Organização
- Agrupar testes por feature usando `@Nested`
- Usar `@BeforeEach` e `@AfterEach` para setup/cleanup
- Manter testes isolados e independentes

### 3. Testcontainers
- Usar uma única configuração centralizada
- Reutilizar container quando possível (padrão Singleton)
- Usar `@ServiceConnection` para auto-configuração

### 4. Mocks
- Usar `@MockBean` para substituir beans do Spring
- Usar `@Mock` e `@InjectMocks` para testes unitários puros
- Verificar chamadas de métodos com `verify()`

## 📚 Referências

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Testcontainers](https://testcontainers.com/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [PIT Mutation Testing](https://pitest.org/)

---

**Última atualização**: 2025-10-13
**Versão do Spring Boot**: 3.5.6
**Versão do Java**: 25
**Status dos Testes**: ✅ TODOS PASSANDO
