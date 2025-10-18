# 🧪 Guia de Testes - Catalog Service

## 📋 Índice
- [Status Geral](#-status-geral-dos-testes)
- [Visão Geral](#-visão-geral)
- [Stack de Testes](#️-stack-de-testes)
- [Comandos Rápidos](#-comandos-rápidos)
- [1. Testes Unitários](#1️⃣-testes-unitários-ut)
- [2. Testes de Integração](#2️⃣-testes-de-integração-it)
- [3. Testes de Contrato](#3️⃣-testes-de-contrato-contract-tests)
- [4. Testes BDD](#4️⃣-testes-bdd-cucumber)
- [5. Testes de Arquitetura](#5️⃣-testes-de-arquitetura-archunit)
- [6. Cobertura de Código](#6️⃣-cobertura-de-código-jacoco)
- [7. Testes de Mutação](#7️⃣-testes-de-mutação-pit)
- [8. Checkstyle](#8️⃣-checkstyle)
- [Configurações](#️-configurações-e-dependências)
- [Boas Práticas](#-boas-práticas)
- [Próximos Passos](#-próximos-passos-recomendados)

---

## 🎯 Status Geral dos Testes

### ✅ Conquistas
- **169 testes executados**: 100% passando
- **KitchenService**: 100% de cobertura
- **RestaurantService**: 100% de cobertura
- **ProductService**: 90% de cobertura
- **Controllers**: 97% de cobertura (Kitchen e Restaurant 100%, Product 97%)
- **Contract Tests**: 44 testes validando schemas JSON e status codes
- **Architecture Tests**: 13 testes passando (6 classes de teste ArchUnit)
- **Checkstyle**: 0 violações

### ⚠️ Áreas de Melhoria
- **Cobertura Total**: 97% (meta: 90%) ✅ META ATINGIDA
- **Testes de Mutação**: 62% (meta: 90%) - **CRÍTICO**
- **ProductService**: 90% (melhoria significativa)
- **Mutações Sobreviventes**: 2 (NullReturnValsMutator)
- **Mutações Sem Cobertura**: 21

### 📊 Métricas Principais
| Métrica | Atual | Meta | Status |
|---------|-------|------|--------|
| Testes Passando | 100% (169/169) | 100% | ✅ |
| Cobertura de Linha | 97% | 90% | ✅ |
| Mutações Eliminadas | 62% | 90% | ❌ |
| Violações Checkstyle | 0 | 0 | ✅ |
| Testes de Arquitetura | 13/13 | 13/13 | ✅ |

---

## 📋 Visão Geral

Este projeto implementa **8 tipos de testes**:

1. **Unit Tests (UT)**: Testes unitários com JUnit 5 + Mockito
2. **Integration Tests (IT)**: Testes de integração com Spring Boot Test + Testcontainers
3. **Contract Tests**: Testes de contrato de API REST com Rest Assured + JSON Schema Validator
4. **BDD Tests**: Testes BDD com Cucumber 7 + JUnit Platform
5. **Architecture Tests**: Testes de arquitetura com ArchUnit + JUnit 5
6. **Code Coverage**: Cobertura de código com JaCoCo
7. **Mutation Tests**: Testes de mutação com PIT
8. **Code Quality**: Validação de qualidade com Checkstyle

---

## 🛠️ Stack de Testes

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| JUnit 5 Platform | 5.12.2 | Framework de testes |
| Mockito | Incluído no Spring Boot Test | Mocks e stubs |
| Spring Boot Test | 3.5.6 | Testes de integração |
| Testcontainers | PostgreSQL 17.6 | Containers para testes |
| Rest Assured | 5.5.6 | Testes de API REST |
| JSON Schema Validator | 5.5.6 | Validação de schemas JSON |
| Cucumber | 7.22.2 | Testes BDD |
| ArchUnit | 1.4.1 | Testes de arquitetura |
| JaCoCo | 0.8.14 | Cobertura de código |
| PIT | 1.21.0 | Testes de mutação |
| Checkstyle | 3.6.0 | Qualidade de código |

---

## ⚡ Comandos Rápidos

```bash
# Executar TODOS os testes (UT + IT + BDD + Contract + Architecture)
./mvnw clean verify

# Executar SOMENTE testes unitários
./mvnw clean test

# Executar SOMENTE testes de integração (IT + BDD + Contract)
./mvnw verify "-Dskip.ut=true" "-Dskip.it=false"

# Executar testes de mutação
./mvnw clean test pitest:mutationCoverage

# Executar checkstyle
./mvnw checkstyle:check

# Pular todos os testes
./mvnw clean install -DskipTests
```

---

## 1️⃣ Testes Unitários (UT)

### 📝 Descrição
Testes unitários validam unidades isoladas de código (classes, métodos) usando mocks para dependências externas.

### 🎯 Comando
```bash
./mvnw clean test
```

### 📊 Resultado Atual
- **Total**: 84 testes
- **Sucesso**: 84 ✅
- **Falhas**: 0
- **Tempo**: ~12 segundos

### 📁 Estrutura
```
src/test/java/
├── api/v1/controller/
│   ├── KitchenControllerTest.java        # 8 testes
│   └── RestaurantControllerTest.java     # 16 testes
├── domain/service/
│   ├── KitchenServiceTest.java           # 21 testes
│   └── RestaurantServiceTest.java        # 29 testes
└── architecture/
    ├── LayeredArchitectureTest.java      # 1 teste
    ├── NamingConventionTest.java         # 5 testes
    ├── SpringAnnotationTest.java         # 4 testes
    └── DependencyTest.java               # 3 testes
```

### 🧪 Cobertura por Classe
| Classe | Testes | Cobertura |
|--------|--------|-----------|
| KitchenController | 8 | 100% |
| RestaurantController | 16 | 100% |
| KitchenService | 21 | 100% |
| RestaurantService | 29 | 100% |

### 💡 Exemplo de Teste
```java
@WebMvcTest(KitchenController.class)
class KitchenControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private KitchenService kitchenService;
    
    @Test
    @DisplayName("Deve criar uma cozinha com sucesso")
    void shouldCreateKitchen() throws Exception {
        // Given
        KitchenRequest request = new KitchenRequest("Italiana");
        KitchenModel model = new KitchenModel(UUID.randomUUID(), "Italiana");
        when(kitchenService.create(any())).thenReturn(model);
        
        // When & Then
        mockMvc.perform(post("/v1/kitchens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Italiana"));
    }
}
```

---

## 2️⃣ Testes de Integração (IT)

### 📝 Descrição
Testes de integração validam a aplicação com todas as camadas integradas, incluindo banco de dados real (via Testcontainers).

### 🎯 Comando
```bash
./mvnw verify "-Dskip.ut=true" "-Dskip.it=false"
```

### 📊 Resultado Atual
- **Total**: 85 testes
  - **Integration Tests (Controllers IT)**: 27 testes
  - **BDD/Cucumber Tests**: 14 cenários
  - **Contract Tests**: 44 testes (incluídos nos IT)
- **Sucesso**: 85 ✅
- **Falhas**: 0
- **Tempo**: ~50 segundos

### 📁 Estrutura
```
src/test/java/
└── api/v1/controller/
    ├── KitchenControllerIT.java          # 10 testes
    └── RestaurantControllerIT.java       # 17 testes
```

### 🐳 Testcontainers
Todos os testes de integração usam PostgreSQL via Testcontainers:

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

### 💡 Exemplo de Teste
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("integration")
class KitchenControllerIT {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("Deve criar uma cozinha com sucesso")
    void shouldCreateKitchen() {
        // Given
        KitchenRequest request = new KitchenRequest("Italiana");
        
        // When
        ResponseEntity<KitchenResponse> response = restTemplate
                .postForEntity("/v1/kitchens", request, KitchenResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getName()).isEqualTo("Italiana");
    }
}
```

---

## 3️⃣ Testes de Contrato (Contract Tests)

### 📝 Descrição
Testes de contrato validam que a API REST atende aos contratos definidos, incluindo schemas JSON, headers HTTP e status codes corretos.

### 🎯 Comando
```bash
# Contract tests estão incluídos nos Integration Tests
./mvnw verify "-Dskip.ut=true" "-Dskip.it=false"

# Ou executar testes específicos de contrato
./mvnw test -Dtest=*ContractIT
```

### 📊 Resultado Atual
- **KitchenControllerContractIT**: 19 testes ✅
- **RestaurantControllerContractIT**: 25 testes ✅
- **Total**: 44 testes de contrato

### 🔍 O que é validado
✅ **Schemas JSON**: Estrutura das respostas  
✅ **Status Codes**: Códigos HTTP corretos  
✅ **Headers**: Content-Type, Location, etc.  
✅ **Validações**: Erros de validação (400 Bad Request)  
✅ **Recursos não encontrados**: 404 Not Found  
✅ **Conflitos**: 409 Conflict  

### 📁 Schemas JSON
```
src/test/resources/schemas/
├── kitchen-response-schema.json
├── kitchen-page-response-schema.json
├── restaurant-create-response-schema.json
├── restaurant-page-response-schema.json
├── restaurant-response-schema.json
├── problem-details-error-schema.json
└── error-response-schema.json
```

### 💡 Exemplo de Teste
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("integration")
class KitchenControllerContractIT {
    
    @LocalServerPort
    private int port;
    
    @Test
    @DisplayName("POST /v1/kitchens - Deve criar cozinha e validar schema")
    void shouldCreateKitchenAndValidateSchema() {
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(Map.of("name", "Italiana"))
        .when()
            .post("/v1/kitchens")
        .then()
            .statusCode(201)
            .header("Content-Type", "application/json")
            .header("Location", notNullValue())
            .body(matchesJsonSchemaInClasspath("schemas/kitchen-response-schema.json"))
            .body("name", equalTo("Italiana"));
    }
}
```

### 🎯 Endpoints Testados

#### Kitchen API
- ✅ `POST /v1/kitchens` - Criar cozinha (5 testes)
- ✅ `GET /v1/kitchens/{id}` - Buscar por ID (3 testes)
- ✅ `GET /v1/kitchens` - Listar com paginação (4 testes)
- ✅ `PUT /v1/kitchens/{id}` - Atualizar cozinha (4 testes)
- ✅ `DELETE /v1/kitchens/{id}` - Deletar cozinha (3 testes)

#### Restaurant API
- ✅ `POST /v1/restaurants` - Criar restaurante (6 testes)
- ✅ `GET /v1/restaurants/{id}` - Buscar por ID (4 testes)
- ✅ `GET /v1/restaurants` - Listar com paginação (5 testes)
- ✅ `PUT /v1/restaurants/{id}` - Atualizar restaurante (4 testes)
- ✅ `DELETE /v1/restaurants/{id}` - Deletar restaurante (4 testes)
- ✅ Validações gerais de headers e Content-Type (2 testes)

---

## 4️⃣ Testes BDD (Cucumber)

### 📝 Descrição
Testes BDD (Behavior Driven Development) escritos em Gherkin descrevem comportamentos da aplicação em linguagem natural.

### 🎯 Comando
```bash
# BDD tests estão incluídos nos Integration Tests
./mvnw verify "-Dskip.ut=true" "-Dskip.it=false"

# Filtrar por tags
./mvnw verify "-Dcucumber.filter.tags=@smoke"
./mvnw verify "-Dcucumber.filter.tags=@regression"
./mvnw verify "-Dcucumber.filter.tags=not @wip"
```

### 📊 Resultado Atual
- **Total**: 14 cenários ✅
- **Kitchen Features**: 8 cenários
- **Restaurant Features**: 6 cenários
- **Steps**: 62 steps passando

### 📁 Estrutura
```
src/test/
├── java/
│   └── cucumber/
│       ├── CucumberIT.java           # Suite BDD
│       └── step/
│           ├── KitchenStep.java      # Step Definitions
│           └── RestaurantStep.java   # Step Definitions
└── resources/
    └── features/
        ├── kitchen.feature           # 8 cenários
        └── restaurant.feature        # 6 cenários
```

### 🎬 Cenários Testados

#### Kitchen Features
```gherkin
@smoke @regression
Funcionalidade: Gestão de Cozinhas

  Cenário: Criar uma nova cozinha com sucesso
  Cenário: Buscar uma cozinha por ID
  Cenário: Listar cozinhas com paginação
  Cenário: Atualizar uma cozinha existente
  Cenário: Deletar uma cozinha
  Cenário: Tentar criar cozinha com nome duplicado
  Cenário: Buscar cozinha inexistente
  Cenário: Validar campos obrigatórios
```

#### Restaurant Features
```gherkin
@smoke @regression
Funcionalidade: Gestão de Restaurantes

  Cenário: Criar um novo restaurante com sucesso
  Cenário: Buscar um restaurante por ID
  Cenário: Listar restaurantes com paginação
  Cenário: Atualizar um restaurante existente
  Cenário: Deletar um restaurante
  Cenário: Tentar criar restaurante com nome duplicado
```

### 💡 Exemplo de Feature
```gherkin
# language: pt
@smoke @regression
Funcionalidade: Gestão de Cozinhas

  Cenário: Criar uma nova cozinha com sucesso
    Dado que o serviço de catálogo está disponível
    Quando eu crio uma cozinha com o nome "Italiana"
    Então a cozinha deve ser criada com sucesso
    E o status code deve ser 201
    E a resposta deve conter o nome "Italiana"

  Cenário: Buscar uma cozinha por ID
    Dado que o serviço de catálogo está disponível
    E que existe uma cozinha cadastrada com o nome "Japonesa"
    Quando eu busco a cozinha pelo ID
    Então o status code deve ser 200
    E a resposta deve conter o nome "Japonesa"
```

---

## 5️⃣ Testes de Arquitetura (ArchUnit)

### 📝 Descrição
Testes de arquitetura validam regras arquiteturais do projeto, como dependências entre camadas, convenções de nomenclatura e uso correto de anotações.

### 🎯 Comando
```bash
# Architecture tests são executados junto com os unit tests
./mvnw test

# Executar somente testes de arquitetura
./mvnw test -Dtest=*ArchitectureTest,*ConventionTest,*AnnotationTest,*DependencyTest
```

### 📊 Resultado Atual
- **Total**: 13 testes ✅
- **Falhas**: 0
- **Classes de teste**: 6

### 📁 Estrutura
```
src/test/java/architecture/
├── LayeredArchitectureTest.java      # 1 teste - Arquitetura em camadas
├── NamingConventionTest.java         # 5 testes - Convenções de nomenclatura
├── SpringAnnotationTest.java         # 4 testes - Anotações Spring
├── SecurityAndPerformanceTest.java   # - Segurança e performance
├── RestApiTest.java                  # - Padrões REST API
└── DependencyTest.java               # 3 testes - Validação de dependências
```

### 🏗️ Regras Validadas

#### 1. Arquitetura em Camadas
✅ API pode acessar Domain, Core e Config  
✅ Domain pode acessar Infrastructure e Core  
✅ Infrastructure pode acessar apenas Core  
✅ Core não depende de nenhuma camada  
✅ Config pode acessar Domain, Infrastructure e Core  

#### 2. Convenções de Nomenclatura
✅ Controllers terminam com "Controller"  
✅ Services terminam com "Service"  
✅ Repositories terminam com "Repository"  
✅ Entities estão no pacote `domain.entity`  
✅ Request/Response estão nos pacotes corretos  

#### 3. Anotações Spring
✅ Controllers usam `@RestController`  
✅ Services usam `@Service`  
✅ Repositories estendem `JpaRepository`  
✅ Entities usam `@Entity`  
❌ Proíbe `System.out.println`  
❌ Services não devem depender de Controllers  

#### 4. Dependências
❌ Domain não deve depender da API  
❌ Infrastructure não deve depender da API  
❌ Core não deve depender de Spring Web  
❌ Entities não devem depender do Spring  

### 💡 Exemplo de Teste
```java
@AnalyzeClasses(packages = "io.github.wesleyosantos91.catalog")
class LayeredArchitectureTest {
    
    @ArchTest
    static final ArchRule layered_architecture = layeredArchitecture()
        .consideringAllDependencies()
        
        .layer("API").definedBy("..api..")
        .layer("Domain").definedBy("..domain..")
        .layer("Infrastructure").definedBy("..infrastructure..")
        .layer("Core").definedBy("..core..")
        .layer("Config").definedBy("..config..")
        
        .whereLayer("API").mayOnlyAccessLayers("Domain", "Core", "Config")
        .whereLayer("Domain").mayOnlyAccessLayers("Infrastructure", "Core")
        .whereLayer("Infrastructure").mayOnlyAccessLayers("Core")
        .whereLayer("Core").mayNotAccessAnyLayer()
        .whereLayer("Config").mayOnlyAccessLayers("Domain", "Infrastructure", "Core");
}
```

### 📁 Estrutura de Pacotes Validada
```
src/main/java/io/github/wesleyosantos91/catalog/
├── api/                    # Camada de apresentação
│   ├── exception/         # Tratamento de exceções
│   └── v1/
│       ├── controller/    # REST Controllers
│       ├── request/       # DTOs de entrada
│       └── response/      # DTOs de saída
├── config/                # Configurações Spring
├── core/                  # Utilitários e cross-cutting
│   ├── mapper/           # Mapeamento entre camadas
│   └── validation/       # Validações customizadas
├── domain/               # Lógica de negócio
│   ├── entity/          # Entidades JPA
│   ├── repository/      # Interfaces de repositório
│   └── service/         # Serviços de domínio
└── infrastructure/      # Implementações técnicas
    ├── filter/          # Filtros HTTP
    ├── metric/          # Métricas e observabilidade
    └── openapi/         # Documentação API
```

---

## 6️⃣ Cobertura de Código (JaCoCo)

### 📝 Descrição
JaCoCo mede a cobertura de código pelos testes, mostrando quais linhas foram executadas.

### 🎯 Comando
```bash
./mvnw clean verify
```

### 📊 Resultado Atual
- **Cobertura**: 97% (373/385 linhas)
- **Meta**: 90%
- **Status**: ✅ META ATINGIDA

### 📈 Cobertura por Classe
| Classe | Linhas Cobertas | Total | Cobertura | Status |
|--------|-----------------|-------|-----------|--------|
| KitchenService | 63 | 63 | 100% | ✅ |
| RestaurantService | 75 | 75 | 100% | ✅ |
| KitchenController | 38 | 38 | 100% | ✅ |
| RestaurantController | 38 | 38 | 100% | ✅ |
| ProductService | 68 | 76 | 90% | ✅ |
| ProductController | 36 | 38 | 97% | ✅ |

### 🚫 Exclusões Configuradas
O JaCoCo exclui automaticamente:
- `Application.class`
- Entidades (`entity/*`)
- DTOs (`request/*`, `response/*`)
- Exceções (`exception/*`)
- Mappers (`mapper/*`)
- Métricas (`metrics/*`)
- Infraestrutura (`infrastructure/**`)

### 📊 Relatório
```bash
# Gerar relatório HTML
./mvnw clean verify

# Visualizar relatório
target/site/jacoco/index.html
```

---

## 7️⃣ Testes de Mutação (PIT)

### 📝 Descrição
Testes de mutação validam a qualidade dos testes, introduzindo pequenas mudanças (mutações) no código e verificando se os testes detectam essas mudanças.

### 🎯 Comando
```bash
./mvnw clean test pitest:mutationCoverage
```

### 📊 Resultado Atual
- **Score de Mutação**: 62% ❌ (meta: 90%)
- **Força dos Testes**: 95% ✅
- **Mutações Geradas**: 60
- **Mutações Eliminadas**: 37 (62%)
- **Mutações Sobreviventes**: 2 ⚠️
- **Sem Cobertura**: 21 ⚠️

### 📈 Métricas Detalhadas
| Métrica | Valor | Status |
|---------|-------|--------|
| Classes Analisadas | 6 | ℹ️ |
| Cobertura de Linha | 97% | ✅ |
| Cobertura de Mutação | 62% | ❌ |
| Força dos Testes | 95% | ✅ |
| Mutações Geradas | 60 | ℹ️ |
| Mutações Eliminadas | 37 | ℹ️ |
| Mutações Sobreviventes | 2 | ⚠️ |
| Sem Cobertura | 21 | ⚠️ |

### 🔬 Análise por Mutador
| Mutador | Geradas | Eliminadas | Sobreviv. | Sem Cob. | Efetividade |
|---------|---------|------------|-----------|----------|-------------|
| VoidMethodCallMutator | 6 | 4 | 0 | 2 | 67% ✅ |
| NullReturnValsMutator | 32 | 20 | 2 | 10 | 63% ⚠️ |
| NegateConditionalsMutator | 22 | 13 | 0 | 9 | 59% ⚠️ |

### 🚨 Pontos Críticos
1. **Mutações Sobreviventes (2)**: Métodos com retornos null não validados
2. **Mutações Sem Cobertura (21)**: Principalmente em ProductService e ProductController
3. **Lacunas de Teste**: Condicionais complexas e edge cases

### 📊 Relatório
```bash
# Gerar relatório HTML
./mvnw clean test pitest:mutationCoverage

# Visualizar relatório
target/pit-reports/index.html
```

### 🎯 Evolução do Score
| Execução | Score | Mutações | Eliminadas | Sobreviv. | Sem Cob. |
|----------|-------|----------|------------|-----------|----------|
| 1ª | 43% | 91 | 39 | 24 | 28 |
| 2ª | 60% | 63 | 38 | 4 | 21 |
| 3ª | 64% | 58 | 37 | 0 | 21 |
| 4ª (Atual) | 62% | 60 | 37 | 2 | 21 |

**📈 Progresso**: +19% desde a primeira execução

---

## 8️⃣ Checkstyle

### 📝 Descrição
Checkstyle valida a qualidade do código verificando convenções de estilo, boas práticas e padrões de codificação.

### 🎯 Comando
```bash
./mvnw checkstyle:check
```

### 📊 Resultado Atual
- **Violações**: 0 ✅
- **Status**: BUILD SUCCESS

### ⚙️ Configuração
- **Arquivo**: `checkstyle.xml`
- **Modo**: `failOnViolation=true`
- **Resultado atual**: ✅ 0 violações

### 📊 Relatório
```bash
# Gerar relatório XML
./mvnw checkstyle:check

# Visualizar relatório
target/checkstyle-result.xml
```

---

## ⚙️ Configurações e Dependências

### Plugins Maven
```xml
<plugins>
    <!-- Testes Unitários -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.5.4</version>
    </plugin>
    
    <!-- Testes de Integração -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-failsafe-plugin</artifactId>
        <version>3.5.4</version>
    </plugin>
    
    <!-- Cobertura de Código -->
    <plugin>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <version>0.8.14</version>
    </plugin>
    
    <!-- Testes de Mutação -->
    <plugin>
        <groupId>org.pitest</groupId>
        <artifactId>pitest-maven</artifactId>
        <version>1.21.0</version>
    </plugin>
    
    <!-- Checkstyle -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-checkstyle-plugin</artifactId>
        <version>3.6.0</artifactId>
    </plugin>
</plugins>
```

### Dependências de Teste
```xml
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Testcontainers -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-testcontainers</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Rest Assured -->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <version>5.5.6</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>json-schema-validator</artifactId>
        <version>5.5.6</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Cucumber -->
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-java</artifactId>
        <version>7.22.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-spring</artifactId>
        <version>7.22.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-junit-platform-engine</artifactId>
        <version>7.22.2</version>
        <scope>test</scope>
    </dependency>
    
    <!-- ArchUnit -->
    <dependency>
        <groupId>com.tngtech.archunit</groupId>
        <artifactId>archunit-junit5</artifactId>
        <version>1.4.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 📝 Boas Práticas

### 1. Nomenclatura de Testes
- **Unit Tests**: `*Test.java` (executados pelo Surefire)
- **Integration Tests**: `*IT.java` (executados pelo Failsafe)
- **Contract Tests**: `*ContractIT.java` (executados pelo Failsafe)
- Usar padrão Given-When-Then nos testes BDD
- Usar `@DisplayName` para descrever cenários em português

### 2. Organização
- Agrupar testes por feature usando `@Nested`
- Usar `@BeforeEach` e `@AfterEach` para setup/cleanup
- Manter testes isolados e independentes

### 3. Testcontainers
- Usar uma única configuração centralizada (`TestcontainersConfiguration`)
- Reutilizar container quando possível (padrão Singleton)
- Usar `@ServiceConnection` para auto-configuração

### 4. Mocks
- Usar `@MockBean` para substituir beans do Spring
- Usar `@Mock` e `@InjectMocks` para testes unitários puros
- Verificar chamadas de métodos com `verify()`

### 5. Contract Tests
- Usar Rest Assured para testes de API REST
- Validar schemas JSON das respostas
- Testar headers HTTP corretos
- Cobrir todos os endpoints e status codes
- Usar Testcontainers para ambiente isolado

---

## 🎯 Próximos Passos Recomendados

### 1. 🔥 Prioridade ALTA - Eliminar Mutações Sobreviventes (2 → 0)
```bash
# Investigar e corrigir as 2 mutações sobreviventes
./mvnw pitest:mutationCoverage -Dverbose=true

# Abrir relatório detalhado
target/pit-reports/index.html
```

**Ações específicas:**
- 🔥 **CRÍTICO**: Adicionar asserções para validar retornos null (2 mutações sobreviventes)
- 🎯 **FOCO ATUAL**: Reduzir mutações sem cobertura (21 restantes)
- 📈 **PROGRESSO**: Score mantém-se em 62% (+19% desde a primeira execução)

### 2. ✅ Prioridade MÉDIA - Consolidar Cobertura em ProductService (90% → 95%)
**ProductService (90% ✅)**
- ✅ Implementação completa de testes unitários
- ✅ Cenários de validação e tratamento de exceções cobertos
- 🎯 Melhorar cobertura para 95% com edge cases adicionais

**ProductController (97% ✅)**
- ✅ Testes de integração implementados (ProductControllerIT)
- ✅ Testes de contrato implementados (ProductControllerContractIT)
- ✅ Endpoints REST testados com diferentes cenários
- 🎯 Alcançar 100% com testes de imagem e validações adicionais

### 3. 📊 Prioridade MÉDIA - Otimizar Testes de Mutação (62% → 90%)
- Adicionar testes para cobrir as 21 mutações sem cobertura
- Melhorar testes condicionais e edge cases
- Validar cenários de exceção específicos

### 4. ✨ Prioridade BAIXA - Otimizações
- ✅ **Manter qualidade atual dos serviços principais**
- ✅ **Força dos testes em 95% (mantida)**
- ✅ **Monitorar efetividade dos mutadores**
- 📊 **Adicionar mais cenários BDD para Products**
- 🔧 **Configurar pipeline CI/CD com gates de qualidade**

---

## 📈 Relatórios Disponíveis

### Cobertura de Código (JaCoCo)
```bash
target/site/jacoco/index.html
```

### Testes de Mutação (PIT)
```bash
target/pit-reports/index.html
```

### Checkstyle
```bash
target/checkstyle-result.xml
```

---

## ✅ Conclusão

O projeto apresenta uma **base sólida de testes** com 169 testes executando corretamente e 100% de cobertura nos serviços principais (Kitchen e Restaurant).

**Pontos fortes:**
- ✅ Estrutura de testes bem organizada
- ✅ Boa separação entre unit, integration, contract, BDD e architecture tests
- ✅ Cobertura completa dos serviços principais
- ✅ Qualidade de código mantida (0 violações Checkstyle)
- ✅ Testes de arquitetura garantindo consistência

**Áreas críticas para melhoria:**
- ❌ **Testes de mutação em 62%** requerem atenção imediata
- 🎯 **ProductService/Controller** têm cobertura adequada (90%/97%), foco em otimização
- 🎯 Foco em testes que validem comportamentos específicos e qualidade de código

---

📚 **Referências**
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Testcontainers](https://www.testcontainers.org/)
- [Rest Assured](https://rest-assured.io/)
- [Cucumber](https://cucumber.io/docs/cucumber/)
- [ArchUnit](https://www.archunit.org/)
- [JaCoCo](https://www.jacoco.org/jacoco/)
- [PIT Mutation Testing](https://pitest.org/)
- [Checkstyle](https://checkstyle.sourceforge.io/)
