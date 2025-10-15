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
│       ├── ApplicationTests.java                       # ✅ Smoke test da aplicação
│       ├── TestcontainersConfiguration.java            # ✅ Configuração Testcontainers (ÚNICA)
│       ├── api/v1/controller/
│       │   ├── KitchenControllerTest.java              # ✅ UT - Controller (8 testes)
│       │   ├── KitchenControllerIT.java                # ✅ IT - Controller (10 testes)
│       │   ├── RestaurantControllerTest.java           # ✅ UT - Controller (16 testes)
│       │   └── RestaurantControllerIT.java             # ✅ IT - Controller (17 testes)
│       ├── domain/service/
│       │   ├── KitchenServiceTest.java                 # ✅ UT - Service (12 testes)
│       │   └── RestaurantServiceTest.java              # ✅ UT - Service (29 testes)
│       └── cucumber/
│           ├── CucumberIT.java                         # ✅ Suite BDD (14 cenários)
│           ├── step/
│           │   ├── KitchenStep.java                    # ✅ Step Definitions
│           │   └── RestaurantStep.java                 # ✅ Step Definitions
│           └── utils/
│               └── FeatureUtils.java                   # ✅ Utilitários
└── resources/
    ├── application-integration.yml                     # ✅ Configuração profile integration
    └── features/
        ├── kitchen.feature                             # ✅ Cenários BDD Kitchen (8 cenários)
        └── restaurant.feature                          # ✅ Cenários BDD Restaurant (6 cenários)
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
mvn clean test "-Dskip.it=true"
```
✅ **Resultado atual: 65 testes passando (0 falhas)**

### Executar SOMENTE Integration Tests (Failsafe + BDD)
```bash
mvn clean verify "-Dskip.ut=true"
```
✅ **Resultado atual: 41 testes executados (0 falhas, 41 passando)**

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
mvn clean install "-Dskip.ut=true" "-Dskip.it=true"
```

## 🏷️ Filtrar Testes BDD por Tags

### Filtrar somente testes @smoke
```bash
mvn verify "-Dcucumber.filter.tags=@smoke"
```

### Filtrar somente testes @regression
```bash
mvn verify "-Dcucumber.filter.tags=@regression"
```

### Excluir testes @wip (work in progress)
```bash
mvn verify "-Dcucumber.filter.tags=not @wip"
```

## 📊 Resultados dos Testes

### Resumo Geral
- **Total de Testes**: 106 testes (65 UT + 41 IT)
- **Sucesso**: 106 ✅
- **Falhas**: 0
- **Status**: ✅ Todos os testes passando

### Unit Tests (Surefire)
- **Total**: 65 testes
- **Sucesso**: 65 ✅
- **Falhas**: 0
- **Tempo**: ~17 segundos

### Integration Tests (Failsafe)
- **Total**: 41 testes (27 IT + 14 BDD)
- **Sucesso**: 41 ✅
- **Falhas**: 0 ✅
- **Tempo**: ~35 segundos

#### Testes de Integração (Controllers IT)
**KitchenControllerIT:**
- ✅ DELETE /v1/kitchens/{id} - Deletar cozinha (2 testes)
- ✅ PUT /v1/kitchens/{id} - Atualizar cozinha (2 testes)
- ✅ GET /v1/kitchens - Listar cozinhas (2 testes)
- ✅ GET /v1/kitchens/{id} - Buscar cozinha por ID (2 testes)
- ✅ POST /v1/kitchens - Criar cozinha (2 testes)

**RestaurantControllerIT:**
- ✅ DELETE /v1/restaurants/{id} - Deletar restaurante (2 testes)
- ✅ PUT /v1/restaurants/{id} - Atualizar restaurante (4 testes)
- ✅ GET /v1/restaurants - Listar restaurantes (5 testes)
- ✅ GET /v1/restaurants/{id} - Buscar restaurante por ID (2 testes)
- ✅ POST /v1/restaurants - Criar restaurante (4 testes)

#### Testes BDD/Cucumber
**Kitchen Features:**
- ✅ Criar uma nova cozinha com sucesso
- ✅ Buscar uma cozinha por ID
- ✅ Listar cozinhas com paginação
- ✅ Atualizar uma cozinha existente
- ✅ Deletar uma cozinha
- ✅ Tentar criar cozinha com nome duplicado
- ✅ Buscar cozinha inexistente

**Restaurant Features:**
- ✅ Criar um novo restaurante com sucesso
- ✅ Buscar um restaurante por ID
- ✅ Listar restaurantes com paginação
- ✅ Atualizar um restaurante existente
- ✅ Deletar um restaurante
- ✅ Tentar criar restaurante com nome duplicado
- ✅ Buscar restaurante inexistente

## 📈 Cobertura de Código (JaCoCo)

### Configuração
- **Mínimo de cobertura**: 90% (LINE coverage)
- **Status atual**: ⚠️ 60% (abaixo do mínimo configurado)
- **Exclusões**:
  - Application.class
  - Entidades (entity/*)
  - DTOs (request/*, response/*)
  - Exceções (exception/*)
  - Mappers (mapper/*)
  - Métricas (metrics/*)
  - Infraestrutura (infrastructure/**)

### Detalhamento por Classe
| Classe | Linhas Cobertas | Total de Linhas | Cobertura |
|--------|----------------|-----------------|-----------|
| KitchenService | 43 | 63 | 68% |
| RestaurantService | 55 | 75 | 73% |
| KitchenController | 38 | 38 | 100% |
| RestaurantController | 38 | 38 | 100% |
| ProductService | 5 | 76 | 7% |
| ProductController | 2 | 38 | 5% |

### Executar relatório de cobertura
```bash
mvn clean verify
```

Relatório gerado em: `target/site/jacoco/index.html`

**Ação necessária**: Adicionar mais testes para ProductService e ProductController para atingir 90% de cobertura.

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

## ⚠️ Itens de Ação

### Melhoria da Cobertura de Código
Para atingir o objetivo de 90% de cobertura, é necessário:

1. **ProductService (7% → 90%)**
   - Adicionar testes unitários para todos os métodos CRUD
   - Cobrir cenários de validação e tratamento de exceções
   - Testar regras de negócio específicas de produtos

2. **ProductController (5% → 90%)**
   - Criar testes de integração (ProductControllerIT)
   - Testar endpoints REST com diferentes cenários
   - Validar responses e status codes

3. **KitchenService (68% → 90%)**
   - Adicionar testes para cenários de borda
   - Melhorar cobertura de validações

4. **RestaurantService (73% → 90%)**
   - Expandir testes de validação
   - Testar casos de erro adicionais

## 📚 Referências

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Testcontainers](https://testcontainers.com/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [PIT Mutation Testing](https://pitest.org/)

---

**Última atualização**: 2025-10-14
**Versão do Spring Boot**: 3.5.6
**Versão do Java**: 25
**Status dos Testes**: ✅ TODOS PASSANDO
**Cobertura de Código**: ⚠️ 60% (objetivo: 90%)
