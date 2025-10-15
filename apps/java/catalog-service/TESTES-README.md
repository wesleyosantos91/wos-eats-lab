# 🧪 Guia de Testes - Catalog Service

##   Status Geral dos Testes

### ✅ Conquistas
- **112 testes executados**: 100% passando
- **KitchenService**: 100% de cobertura
- **RestaurantService**: 100% de cobertura
- **Controllers**: 100% de cobertura (Kitchen e Restaurant)
- **Checkstyle**: 0 violações

### ⚠️ Áreas de Melhoria
- **Cobertura Total**: 68% (meta: 90%)
- **Testes de Mutação**: 64% (meta: 90%) - **CRÍTICO**
- **ProductService/Controller**: Baixa cobertura

### 🎯 Métricas Principais
| Métrica | Atual | Meta | Status |
|---------|-------|------|--------|
| Cobertura de Linha | 68% | 90% | ⚠️ |
| Mutações Eliminadas | 64% | 90% | ❌ |
| Testes Passando | 100% | 100% | ✅ |
| Violações Checkstyle | 0 | 0 | ✅ |

##  📋 Visão Geral

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
✅ **Resultado atual: 71 testes passando (0 falhas)**

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
- **Total de Testes**: 112 testes (71 UT + 41 IT)
- **Sucesso**: 112 ✅
- **Falhas**: 0
- **Status**: ✅ Todos os testes passando

### Unit Tests (Surefire)
- **Total**: 71 testes
- **Sucesso**: 71 ✅
- **Falhas**: 0
- **Tempo**: ~12 segundos

### Integration Tests (Failsafe)
- **Total**: 41 testes (27 IT + 14 BDD)
- **Sucesso**: 41 ✅
- **Falhas**: 0 ✅
- **Tempo**: ~38 segundos

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
- **Status atual**: ⚠️ 68% (melhorou de 64%, mas ainda abaixo do mínimo configurado)
- **Exclusões**:
    - Application.class
    - Entidades (entity/*)
    - DTOs (request/*, response/*)
    - Exceções (exception/*)
    - Mappers (mapper/*)
    - Métricas (metrics/*)
    - Infraestrutura (infrastructure/**)

### Detalhamento por Classe

| Classe               | Linhas Cobertas | Total de Linhas | Cobertura | Status             |
|----------------------|-----------------|-----------------|-----------|--------------------|
| KitchenService       | 63              | 63              | 100%      | ✅                  |
| RestaurantService    | 75              | 75              | 100%      | ✅                  |
| KitchenController    | 38              | 38              | 100%      | ✅                  |
| RestaurantController | 38              | 38              | 100%      | ✅                  |
| ProductService       | 5               | 76              | 7%        | ❌ Baixa cobertura |
| ProductController    | 2               | 38              | 5%        | ❌ Baixa cobertura |

### Executar relatório de cobertura
```bash
mvn clean verify
```

Relatório gerado em: `target/site/jacoco/index.html`

**Ação necessária**: Adicionar mais testes para ProductService e ProductController para atingir 90% de cobertura.

## 🧬 Testes de Mutação (PIT)

### 📊 Visão Geral do Projeto
- **Classes Analisadas**: 6
- **Cobertura de Linha**: 68% (224/331)
- **Cobertura de Mutação**: 64% (37/58)
- **Força dos Testes**: 100% (37/37)

| Métrica | Valor | Status |
|---------|-------|--------|
| **Score de Mutação** | 64% | ❌ (abaixo de 90%) |
| **Força dos Testes** | 100% | ✅ |
| **Mutações Geradas** | 58 | ℹ️ |
| **Mutações Eliminadas** | 37 | ℹ️ |
| **Mutações Sobreviventes** | 0 | ✅ |
| **Sem Cobertura** | 21 | ⚠️ |

### 🔍 Detalhamento por Package

#### 📁 io.github.wesleyosantos91.catalog.api.v1.controller
- **Classes**: 3 (KitchenController, RestaurantController, ProductController)
- **Cobertura de Linha**: 69% (81/117)
- **Cobertura de Mutação**: 67% (12/18)
- **Força dos Testes**: 100% (12/12)

#### 📁 io.github.wesleyosantos91.catalog.domain.service  
- **Classes**: 3 (KitchenService, RestaurantService, ProductService)
- **Cobertura de Linha**: 67% (143/214)
- **Cobertura de Mutação**: 63% (25/40)
- **Força dos Testes**: 100% (25/25)

### 🔬 Análise por Tipo de Mutador

#### ✅ VoidMethodCallMutator (67% de efetividade)
- **Mutações Geradas**: 6
- **Mutações Eliminadas**: 4 (67%)
- **Sem Cobertura**: 2
- **Status**: ✅ Bom desempenho
- **Ação**: Ampliar testes para métodos void sem cobertura

#### ✅ NullReturnValsMutator (67% de efetividade)  
- **Mutações Geradas**: 30
- **Mutações Eliminadas**: 20 (67%)
- **Sem Cobertura**: 10
- **Status**: ✅ Bom desempenho
- **Ação**: Adicionar testes para cenários de retorno null

#### ⚠️ NegateConditionalsMutator (59% de efetividade)
- **Mutações Geradas**: 22
- **Mutações Eliminadas**: 13 (59%)
- **Sem Cobertura**: 9
- **Status**: ⚠️ Performance moderada
- **Ação**: Melhorar testes condicionais e edge cases

### 🎯 Comparativo de Evoluções

| Execução | Score | Mutações | Eliminadas | Sobreviventes | Sem Cobertura |
|----------|-------|----------|------------|---------------|---------------|
| **1ª** | 43% | 91 | 39 | 24 | 28 |
| **2ª** | 60% | 63 | 38 | 4 | 21 |
| **3ª** | 64% | 58 | 37 | 0 | 21 |

**📈 Progresso**: Score melhorou de 43% → 64% (+21%), eliminação completa de mutações sobreviventes!

### 🚨 Pontos Críticos

#### 1. **Mutações Sem Cobertura (21 restantes)**
```
❌ 21 mutações não são testadas devido à falta de cobertura de código
📍 Localização: Principalmente em ProductService e ProductController
💡 Solução: Adicionar testes unitários e de integração
```

#### 2. **Lacunas de Teste por Package**
```
⚠️ Controllers: 31% das mutações sem cobertura  
⚠️ Services: 38% das mutações sem cobertura
📍 Foco: ProductService (baixa cobertura) e condicionais complexas
```

### 💡 Plano de Ação Priorizado

#### 🔥 **Prioridade ALTA** - Aumentar Cobertura Base
```bash
# Objetivo: 21 mutações sem cobertura → 0
1. ProductService: Implementar testes unitários completos
2. ProductController: Implementar testes de integração
3. Condicionais: Adicionar testes para branches não cobertos
```

#### 📊 **Prioridade MÉDIA** - Otimizar Mutadores
```bash
# Objetivo: NegateConditionalsMutator 59% → 80%
1. Testes de boundary conditions
2. Validação de edge cases  
3. Cenários de exceção específicos
```

#### ✨ **Prioridade BAIXA** - Manter Qualidade
```bash
# Objetivo: Manter força dos testes em 100%
1. Monitorar VoidMethodCallMutator (67%)
2. Monitorar NullReturnValsMutator (67%)
3. Refatorar testes duplicados
```

### 📋 Checklist de Implementação

- [ ] **ProductService**: Implementar 76 linhas de teste (0% → 90%)
- [ ] **ProductController**: Implementar 36 linhas de teste (5% → 90%)  
- [ ] **Condicionais**: Adicionar 9 testes para NegateConditionalsMutator
- [ ] **Métodos Void**: Adicionar 2 testes para VoidMethodCallMutator
- [ ] **Retornos Null**: Adicionar 10 testes para NullReturnValsMutator
- [ ] **Validação**: Executar `mvn pitest:mutationCoverage` após cada implementação

### ⚙️ Configuração e Execução

#### Configuração
- **Plugin**: pitest-maven-plugin 1.21.0
- **JUnit Integration**: pitest-junit5-plugin 1.2.3  
- **Mutation Threshold**: 90%
- **Timeout**: 4000ms por teste
- **Threads**: Automático (baseado no sistema)

#### Executar Testes de Mutação
```bash
# Execução completa
mvn clean test pitest:mutationCoverage

# Com verbose (para debugging)
mvn pitest:mutationCoverage -Dverbose=true

# Focar em classes específicas
mvn pitest:mutationCoverage -DtargetClasses=io.github.wesleyosantos91.catalog.domain.service.*
```

#### Relatórios Gerados
```bash
# Relatório HTML interativo
target/pit-reports/index.html

# Relatório por package
target/pit-reports/io.github.wesleyosantos91.catalog.api.v1.controller/
target/pit-reports/io.github.wesleyosantos91.catalog.domain.service/
```

### 📈 Meta de Melhoria

| Fase | Score Meta | Prazo | Ações Principais |
|------|------------|-------|------------------|
| **Fase 1** | 75% | 1 semana | Implementar ProductService tests |
| **Fase 2** | 85% | 2 semanas | Implementar ProductController tests |
| **Fase 3** | 90% | 3 semanas | Otimizar condicionais e edge cases |

## 🎯 Próximos Passos Recomendados

### 1. Prioridade Alta - Melhorar Testes de Mutação (64% → 90%)
```bash
# Investigar mutações sem cobertura específicas
mvn pitest:mutationCoverage -Dverbose=true
# Abrir relatório detalhado
target/pit-reports/index.html
```

**Ações específicas:**
- ✅ **CONCLUÍDO**: Eliminação de todas as mutações sobreviventes (24 → 0)
- 🎯 **FOCO ATUAL**: Reduzir mutações sem cobertura (21 restantes)
- 📈 **PROGRESSO**: Score melhorou de 43% → 64% (+21%)
- 🔥 **CRÍTICO**: Implementar testes para ProductService (7% cobertura)
- 🔥 **CRÍTICO**: Implementar testes para ProductController (5% cobertura)

### 2. Prioridade Média - Aumentar Cobertura Geral (68% → 90%)
- 🎯 **ProductService (7% → 90%)**
    - Implementar testes unitários para todos os métodos CRUD
    - Cobrir cenários de validação e tratamento de exceções
    - Testar regras de negócio específicas de produtos

- 🎯 **ProductController (5% → 90%)**
    - Criar testes de integração (ProductControllerIT)
    - Testar endpoints REST com diferentes cenários
    - Validar responses e status codes

### 3. Prioridade Baixa - Otimizações
- ✅ **Manter qualidade atual dos serviços principais**
- ✅ **Força dos testes em 100% (mantida)**
- ✅ **Monitorar efetividade dos mutadores**
- 📊 **Adicionar mais cenários BDD para Products**
- 🔧 **Configurar pipeline CI/CD com gates de qualidade**

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

## ✅ Conclusão

O projeto apresenta uma **base sólida de testes** com 71 testes executando corretamente e 100% de cobertura nos serviços principais (Kitchen e Restaurant).

**Pontos fortes:**
- Estrutura de testes bem organizada
- Boa separação entre unit, integration e BDD tests
- Cobertura completa dos serviços principais
- Qualidade de código mantida (0 violações Checkstyle)

**Áreas críticas para melhoria:**
- **Testes de mutação em 43%** requerem atenção imediata
- Implementação completa do ProductService/Controller
- Foco em testes que validem comportamentos específicos, não apenas cobertura

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

**Última atualização**: 2025-10-14
**Versão do Spring Boot**: 3.5.6
**Versão do Java**: 25
**Status dos Testes**: ✅ TODOS PASSANDO (112/112)
**Cobertura de Código**: ⚠️ 68% (objetivo: 90%)
**Testes de Mutação**: ⚠️ 64% (objetivo: 90%)
