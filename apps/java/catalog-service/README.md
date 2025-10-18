# 🍽️ Catalog Service - WOS Eats Lab

![Java](https://img.shields.io/badge/Java-25-007396?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-336791?logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Migrations-Flyway-CC0200?logo=flyway&logoColor=white)
![JUnit](https://img.shields.io/badge/Tests-JUnit_5-25A162?logo=JUnit5&logoColor=white)
![MapStruct](https://img.shields.io/badge/Mapper-MapStruct-FF6F00?logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-1565C0?logo=apache-maven&logoColor=white)

---

> 🎯 **Objetivo:** Microsserviço responsável pelo gerenciamento do catálogo de restaurantes, cozinhas e produtos da plataforma WOS Eats Lab. Base para operações de cadastro, consulta e manutenção do domínio de catálogo.

---

## 🏗️ Arquitetura

- **Hexagonal (Ports & Adapters)** + **Domain-Driven Design (DDD)**
- Separação clara entre domínio, casos de uso, API e infraestrutura
- Persistência em PostgreSQL, migrações com Flyway
- Testes automatizados (unitários, integração, arquitetura)

```text
┌──────────────┐   ┌──────────────┐   ┌───────────────┐
│    API       │   │    Core      │   │   Domain      │
│   (REST)     │──▶│ (Use Cases)  │──▶│  (Entities)   │
└──────────────┘   └──────────────┘   └───────────────┘
        │                │                 │
        ▼                ▼                 ▼
┌────────────────────────────────────────────────────┐
│              Infrastructure (JPA, DB, S3)          │
└────────────────────────────────────────────────────┘
```

### Estrutura dos Pacotes

- `src/main/java/io/github/wesleyosantos91/catalog/domain` — Entidades, agregados, regras de negócio, portas
- `src/main/java/io/github/wesleyosantos91/catalog/core` — Casos de uso, serviços de aplicação, mappers
- `src/main/java/io/github/wesleyosantos91/catalog/api` — Adaptadores de entrada (REST controllers)
- `src/main/java/io/github/wesleyosantos91/catalog/infrastructure` — Adaptadores de saída (JPA, integrações externas)

---

## 🗂️ Domínios do Projeto

O Catalog Service trabalha com três domínios principais, cada um com suas entidades, modelos de domínio e Data Transfer Objects (DTOs) para entrada (request), saída (response) e filtros (query):

- **Kitchen (Cozinha)**
  - Representa o tipo de cozinha de um restaurante (ex: Brasileira, Italiana, Japonesa).
  - **Entidades/Modelos:** `KitchenEntity`, `KitchenModel`
  - **Request:** `KitchenRequest` (criação/atualização), `KitchenQueryRequest` (filtros)
  - **Response:** `KitchenResponse`

- **Restaurant (Restaurante)**
  - Representa o restaurante, incluindo nome, cozinha associada, taxa de entrega, status, etc.
  - **Entidades/Modelos:** `RestaurantEntity`, `RestaurantModel`
  - **Request:** `RestaurantRequest` (criação/atualização), `RestaurantQueryRequest` (filtros)
  - **Response:** `RestaurantResponse`

- **Product (Produto)**
  - Representa os itens do cardápio de cada restaurante, com nome, descrição, preço, disponibilidade, imagem, etc.
  - **Entidades/Modelos:** `ProductEntity`, `ProductModel`
  - **Request:** `ProductRequest` (criação/atualização), `ProductQueryRequest` (filtros)
  - **Response:** `ProductResponse`

- **Tratamento de Erros**
  - Respostas padronizadas de erro para APIs.
  - **Response:** `ErrorResponse`, `CustomProblemDetail`

---

## 📚 API - Endpoints Principais

### 🍽️ Kitchens
- `POST /v1/kitchens`
  Cria uma nova cozinha.
- `GET /v1/kitchens/{id}`
  Busca uma cozinha pelo ID.
- `GET /v1/kitchens`
  Lista/pesquisa cozinhas com filtros e paginação.
- `PUT /v1/kitchens/{id}`
  Atualiza uma cozinha existente.
- `DELETE /v1/kitchens/{id}`
  Remove uma cozinha.

---

### 🍴 Restaurants
- `POST /v1/restaurants`
  Cria um novo restaurante.
- `GET /v1/restaurants/{id}`
  Busca um restaurante pelo ID.
- `GET /v1/restaurants`
  Lista/pesquisa restaurantes com filtros e paginação.
- `PUT /v1/restaurants/{id}`
  Atualiza um restaurante existente.
- `DELETE /v1/restaurants/{id}`
  Remove um restaurante.

---

### 🛒 Products
- `POST /v1/products`
  Cria um novo produto.
- `GET /v1/products/{id}`
  Busca um produto pelo ID.
- `GET /v1/products/{id}/image`
  Busca a imagem de um produto pelo ID.
- `GET /v1/products`
  Lista/pesquisa produtos com filtros e paginação.
- `PUT /v1/products/{id}`
  Atualiza um produto existente.
- `DELETE /v1/products/{id}`
  Remove um produto.
- `DELETE /v1/products/{id}/image`
  Remove a imagem de um produto.

---

## 🚀 Quick Start

### Pré-requisitos

- Java 25+
- Maven
- Docker (opcional, para banco local)

### 🐘 Subir Banco de Dados PostgreSQL

```bash
docker run --name catalog-db -e POSTGRES_DB=catalog -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:15
```

> O schema será criado automaticamente pelas migrações Flyway ao iniciar a aplicação.

### ▶️ Executar a Aplicação

```bash
mvn spring-boot:run
```

Acesse: [http://localhost:8090](http://localhost:8090)

### 🧪 Testar a API

Para testar os endpoints da API, importe a coleção do Insomnia localizada em [collections/catalog-service-collections.yaml](./collections/catalog-service-collections.yaml).

### 🏗️ Build & Testes

```bash
mvn clean install
```

---

## 📁 Estrutura do Projeto

```
catalog-service/
├── src/
│   ├── main/
│   │   ├── java/io/github/wesleyosantos91/catalog/
│   │   │   ├── api/
│   │   │   ├── core/
│   │   │   ├── domain/
│   │   │   └── infrastructure/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/   # Scripts Flyway
│   └── test/
│       └── java/...
├── pom.xml
├── Dockerfile
├── README.md
└── ...
```

---

## ⚙️ Configurações Principais

- **Porta padrão:** 8090 (`src/main/resources/application.yml`)
- **Banco:** PostgreSQL, user/pass: `postgres/postgres`, database: `catalog`
- **Migrações:** `src/main/resources/db/migration/`
- **Testes:** JUnit 5, Mockito, ArchUnit

---

## 🧪 Testes

Para detalhes completos sobre execução, cobertura e exemplos de testes (unitários, integração, contrato, arquitetura), consulte o arquivo:

[TESTES-README.md](./TESTES-README.md)

---

## 🔒 Segurança

- Configuração de autenticação/autorização pode ser expandida conforme necessidade
- Variáveis sensíveis devem ser mantidas fora do versionamento (use profiles ou variáveis de ambiente)

---

## 📈 Observabilidade

- Logs estruturados via Spring Boot
- Pronto para integração com Prometheus/Grafana (caso necessário)

---

## 🤝 Contribuição

1. Fork o repositório
2. Crie uma branch (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Add nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

---

## 📋 Troubleshooting

**Banco não sobe:**

```bash
docker rm -f catalog-db && docker run --name catalog-db -e POSTGRES_DB=catalog -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:15
```

**Porta já em uso:**

- Altere a porta em `src/main/resources/application.yml` ou pare o serviço que está usando a porta 8090.

**Problemas com dependências:**

```bash
mvn clean install -U
```

---

*Este README faz parte do projeto [wos-eats-lab](../../README.md)*
