package io.github.wesleyosantos91.catalog.api.v1.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.github.wesleyosantos91.catalog.TestcontainersConfiguration;
import io.github.wesleyosantos91.catalog.api.v1.request.KitchenRequest;
import io.github.wesleyosantos91.catalog.domain.repository.KitchenRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

/**
 * Testes de contrato para KitchenController usando Rest Assured e JSON Schema Validator.
 * Valida se as respostas da API estão seguindo o contrato definido pelos JSON Schemas.
 * 
 * <p>Os testes verificam:</p>
 * <ul>
 *   <li>Estrutura das respostas JSON</li>
 *   <li>Tipos de dados retornados</li>
 *   <li>Campos obrigatórios</li>
 *   <li>Formato dos dados (UUIDs, strings, etc.)</li>
 *   <li>Status codes HTTP</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("integration")
@DisplayName("KitchenController - Contract Tests")
class KitchenControllerContractIT {

    @Autowired
    private KitchenRepository kitchenRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/v1/kitchens";
        
        // Limpa o banco antes de cada teste para garantir estado inicial limpo
        kitchenRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        // Limpa o banco após cada teste para garantir isolamento
        kitchenRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /v1/kitchens - Criar cozinha")
    class CreateKitchenContract {

        @Test
        @DisplayName("Deve retornar resposta conforme contrato quando criar cozinha com sucesso")
        void deveRetornarRespostaConformeContratoQuandoCriarCozinhaComSucesso() {
            KitchenRequest request = new KitchenRequest("Italiana");

            given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post()
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/kitchen-response-schema.json"))
                .body("id", notNullValue())
                .body("name", equalTo("Italiana"));
        }

        @Test
        @DisplayName("Deve retornar erro conforme contrato quando nome é vazio")
        void deveRetornarErroConformeContratoQuandoNomeVazio() {
            KitchenRequest request = new KitchenRequest("");

            given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post()
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON);
        }

        @Test
        @DisplayName("Deve retornar erro conforme contrato quando nome é null")
        void deveRetornarErroConformeContratoQuandoNomeNull() {
            KitchenRequest request = new KitchenRequest(null);

            given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post()
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON);
        }

        @Test
        @DisplayName("Deve retornar erro conforme contrato quando body é inválido")
        void deveRetornarErroConformeContratoQuandoBodyInvalido() {
            given()
                .contentType(ContentType.JSON)
                .body("{\"invalid\": \"json\"}")
            .when()
                .post()
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON);
        }
    }

    @Nested
    @DisplayName("GET /v1/kitchens/{id} - Buscar cozinha por ID")
    class GetKitchenByIdContract {

        @Test
        @DisplayName("Deve retornar resposta conforme contrato quando buscar cozinha existente")
        void deveRetornarRespostaConformeContratoQuandoBuscarCozinhaExistente() {
            // Cria uma cozinha primeiro
            String kitchenId = given()
                .contentType(ContentType.JSON)
                .body(new KitchenRequest("Japonesa"))
            .when()
                .post()
            .then()
                .extract().path("id");

            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/{id}", kitchenId)
            .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/kitchen-response-schema.json"))
                .body("id", equalTo(kitchenId))
                .body("name", equalTo("Japonesa"));
        }

        @Test
        @DisplayName("Deve retornar erro conforme contrato quando cozinha não existe")
        void deveRetornarErroConformeContratoQuandoCozinhaNaoExiste() {
            UUID nonExistentId = UUID.randomUUID();

            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/{id}", nonExistentId)
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON);
        }

        @Test
        @DisplayName("Deve retornar erro conforme contrato quando ID é inválido")
        void deveRetornarErroConformeContratoQuandoIdInvalido() {
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/{id}", "invalid-uuid")
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON);
        }
    }

    @Nested
    @DisplayName("GET /v1/kitchens - Buscar cozinhas com paginação")
    class SearchKitchensContract {

        @Test
        @DisplayName("Deve retornar resposta conforme contrato quando buscar cozinhas sem filtros")
        void deveRetornarRespostaConformeContratoQuandoBuscarCozinhasSemFiltros() {
            // Cria algumas cozinhas primeiro
            given().contentType(ContentType.JSON).body(new KitchenRequest("Italiana")).post();
            given().contentType(ContentType.JSON).body(new KitchenRequest("Japonesa")).post();

            given()
                .contentType(ContentType.JSON)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/kitchen-page-response-schema.json"))
                .body("content.size()", equalTo(2))
                .body("page.total_elements", equalTo(2))
                .body("page.total_pages", equalTo(1));
        }

        @Test
        @DisplayName("Deve retornar resposta conforme contrato quando buscar cozinhas com filtro por nome")
        void deveRetornarRespostaConformeContratoQuandoBuscarCozinhasComFiltroNome() {
            // Cria algumas cozinhas primeiro
            given().contentType(ContentType.JSON).body(new KitchenRequest("Italiana")).post();
            given().contentType(ContentType.JSON).body(new KitchenRequest("Japonesa")).post();

            given()
                .contentType(ContentType.JSON)
                .queryParam("name", "Italiana")
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/kitchen-page-response-schema.json"))
                .body("content.size()", equalTo(1))
                .body("content[0].name", equalTo("Italiana"));
        }

        @Test
        @DisplayName("Deve retornar resposta conforme contrato quando buscar cozinhas com paginação")
        void deveRetornarRespostaConformeContratoQuandoBuscarCozinhasComPaginacao() {
            // Cria várias cozinhas
            for (int i = 1; i <= 5; i++) {
                given().contentType(ContentType.JSON)
                       .body(new KitchenRequest("Cozinha " + i))
                       .post();
            }

            given()
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 2)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/kitchen-page-response-schema.json"))
                .body("content.size()", equalTo(2))
                .body("page.total_elements", equalTo(5))
                .body("page.total_pages", equalTo(3))
                .body("page.number", equalTo(0))
                .body("page.size", equalTo(2));
        }

        @Test
        @DisplayName("Deve retornar lista vazia conforme contrato quando não há cozinhas")
        void deveRetornarListaVaziaConformeContratoQuandoNaoHaCozinhas() {
            given()
                .contentType(ContentType.JSON)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/kitchen-page-response-schema.json"))
                .body("content.size()", equalTo(0))
                .body("page.total_elements", equalTo(0))
                .body("page.total_pages", equalTo(0));
        }
    }

    @Nested
    @DisplayName("PUT /v1/kitchens/{id} - Atualizar cozinha")
    class UpdateKitchenContract {

        @Test
        @DisplayName("Deve retornar resposta conforme contrato quando atualizar cozinha existente")
        void deveRetornarRespostaConformeContratoQuandoAtualizarCozinhaExistente() {
            // Cria uma cozinha primeiro
            String kitchenId = given()
                .contentType(ContentType.JSON)
                .body(new KitchenRequest("Italiana"))
            .when()
                .post()
            .then()
                .extract().path("id");

            KitchenRequest updateRequest = new KitchenRequest("Italiana Moderna");

            given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
            .when()
                .put("/{id}", kitchenId)
            .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/kitchen-response-schema.json"))
                .body("id", equalTo(kitchenId))
                .body("name", equalTo("Italiana Moderna"));
        }

        @Test
        @DisplayName("Deve retornar erro conforme contrato quando atualizar cozinha inexistente")
        void deveRetornarErroConformeContratoQuandoAtualizarCozinhaInexistente() {
            UUID nonExistentId = UUID.randomUUID();
            KitchenRequest updateRequest = new KitchenRequest("Nova Cozinha");

            given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
            .when()
                .put("/{id}", nonExistentId)
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON);
        }

        @Test
        @DisplayName("Deve retornar erro conforme contrato quando nome de atualização é vazio")
        void deveRetornarErroConformeContratoQuandoNomeAtualizacaoVazio() {
            // Cria uma cozinha primeiro
            String kitchenId = given()
                .contentType(ContentType.JSON)
                .body(new KitchenRequest("Italiana"))
            .when()
                .post()
            .then()
                .extract().path("id");

            KitchenRequest updateRequest = new KitchenRequest("");

            given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
            .when()
                .put("/{id}", kitchenId)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON);
        }
    }

    @Nested
    @DisplayName("DELETE /v1/kitchens/{id} - Deletar cozinha")
    class DeleteKitchenContract {

        @Test
        @DisplayName("Deve retornar resposta conforme contrato quando deletar cozinha existente")
        void deveRetornarRespostaConformeContratoQuandoDeletarCozinhaExistente() {
            // Cria uma cozinha primeiro
            String kitchenId = given()
                .contentType(ContentType.JSON)
                .body(new KitchenRequest("Italiana"))
            .when()
                .post()
            .then()
                .extract().path("id");

            given()
                .contentType(ContentType.JSON)
            .when()
                .delete("/{id}", kitchenId)
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

            // Verifica que a cozinha foi realmente deletada
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/{id}", kitchenId)
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("Deve retornar erro conforme contrato quando deletar cozinha inexistente")
        void deveRetornarErroConformeContratoQuandoDeletarCozinhaInexistente() {
            UUID nonExistentId = UUID.randomUUID();

            given()
                .contentType(ContentType.JSON)
            .when()
                .delete("/{id}", nonExistentId)
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON);
        }

        @Test
        @DisplayName("Deve retornar erro conforme contrato quando ID de deleção é inválido")
        void deveRetornarErroConformeContratoQuandoIdDelecaoInvalido() {
            given()
                .contentType(ContentType.JSON)
            .when()
                .delete("/{id}", "invalid-uuid")
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON);
        }
    }

    @Nested
    @DisplayName("Testes de Headers e Content-Type")
    class HeadersAndContentTypeContract {

        @Test
        @DisplayName("Deve aceitar e retornar Content-Type application/json")
        void deveAceitarERetornarContentTypeApplicationJson() {
            KitchenRequest request = new KitchenRequest("Brasileira");

            given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
            .when()
                .post()
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/kitchen-response-schema.json"));
        }

        @Test
        @DisplayName("Deve retornar erro quando Content-Type não é suportado")
        void deveRetornarErroQuandoContentTypeNaoSuportado() {
            given()
                .contentType(ContentType.XML)
                .body("<kitchen><name>Teste</name></kitchen>")
            .when()
                .post()
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        }
    }
}