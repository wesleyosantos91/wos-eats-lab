package io.github.wesleyosantos91.catalog.api.v1.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import io.github.wesleyosantos91.catalog.TestcontainersConfiguration;
import io.github.wesleyosantos91.catalog.api.v1.request.KitchenRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.RestaurantRequest;
import io.github.wesleyosantos91.catalog.domain.repository.KitchenRepository;
import io.github.wesleyosantos91.catalog.domain.repository.RestaurantRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("integration")
@DisplayName("RestaurantController - Contract Tests")
class RestaurantControllerContractIT {


    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private KitchenRepository kitchenRepository;


    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";

        // Limpa o banco antes de cada teste para garantir estado inicial limpo
        restaurantRepository.deleteAll();
        kitchenRepository.deleteAll();
    }

    // Método helper para criar uma cozinha e retornar o ID
    private String createKitchen(String kitchenName) {
        return given()
                .contentType(ContentType.JSON)
                .body(new KitchenRequest(kitchenName))
                .when()
                .post("/v1/kitchens")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    // Método helper para criar um restaurante e retornar o ID
    private String createRestaurant(String name, String kitchenId, Boolean active, BigDecimal deliveryFee) {
        return given()
                .contentType(ContentType.JSON)
                .body(new RestaurantRequest(name, UUID.fromString(kitchenId), active, deliveryFee))
                .when()
                .post("/v1/restaurants")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    @Nested
    @DisplayName("POST /v1/restaurants - Criar restaurante")
    class CreateRestaurantContract {

        @Test
        @DisplayName("Deve criar restaurante com dados válidos e validar response schema")
        void shouldCreateRestaurantWithValidDataAndValidateSchema() {
            // Given
            String kitchenId = createKitchen("Italiana");
            RestaurantRequest request = new RestaurantRequest(
                    "Restaurante Italiano",
                    UUID.fromString(kitchenId),
                    true,
                    new BigDecimal("10.50")
            );

            // When & Then
            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/v1/restaurants")
                    .then()
                    .statusCode(201)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/restaurant-create-response-schema.json"))
                    .body("id", notNullValue())
                    .body("name", equalTo("Restaurante Italiano"))
                    .body("kitchen.id", equalTo(kitchenId))
                    .body("active", equalTo(true))
                    .body("delivery_fee", equalTo(10.50f));
        }

        @Test
        @DisplayName("Deve validar headers de resposta corretos")
        void shouldValidateCorrectResponseHeaders() {
            // Given
            String kitchenId = createKitchen("Brasileira");
            RestaurantRequest request = new RestaurantRequest(
                    "Churrascaria Gaúcha",
                    UUID.fromString(kitchenId),
                    true,
                    new BigDecimal("15.00")
            );

            // When & Then
            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/v1/restaurants")
                    .then()
                    .statusCode(201)
                    .header("Content-Type", containsString("application/json"))
                    .body("id", notNullValue())
                    .body("name", equalTo("Churrascaria Gaúcha"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando nome estiver vazio")
        void shouldReturn400WhenNameIsEmpty() {
            // Given
            String kitchenId = createKitchen("Japonesa");
            RestaurantRequest request = new RestaurantRequest(
                    "",
                    UUID.fromString(kitchenId),
                    true,
                    new BigDecimal("12.00")
            );

            // When & Then
            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/v1/restaurants")
                    .then()
                    .statusCode(400)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/problem-details-error-schema.json"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando kitchenId for nulo")
        void shouldReturn400WhenKitchenIdIsNull() {
            // Given
            RestaurantRequest request = new RestaurantRequest(
                    "Restaurante Sem Cozinha",
                    null,
                    true,
                    new BigDecimal("8.00")
            );

            // When & Then
            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/v1/restaurants")
                    .then()
                    .statusCode(400)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/problem-details-error-schema.json"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando deliveryFee for negativo")
        void shouldReturn400WhenDeliveryFeeIsNegative() {
            // Given
            String kitchenId = createKitchen("Mexicana");
            RestaurantRequest request = new RestaurantRequest(
                    "Taco Bell",
                    UUID.fromString(kitchenId),
                    true,
                    new BigDecimal("-5.00")
            );

            // When & Then
            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/v1/restaurants")
                    .then()
                    .statusCode(400)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/problem-details-error-schema.json"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando corpo da requisição estiver vazio")
        void shouldReturn400WhenRequestBodyIsEmpty() {
            // When & Then
            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .post("/v1/restaurants")
                    .then()
                    .statusCode(400)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/problem-details-error-schema.json"));
        }
    }

    @Nested
    @DisplayName("GET /v1/restaurants/{id} - Buscar restaurante por ID")
    class GetRestaurantByIdContract {

        @Test
        @DisplayName("Deve buscar restaurante existente e validar response schema")
        void shouldGetExistingRestaurantAndValidateSchema() {
            // Given
            String kitchenId = createKitchen("Chinesa");
            String restaurantId = createRestaurant("China in Box", kitchenId, true, new BigDecimal("8.50"));

            // When & Then
            given()
                    .when()
                    .get("/v1/restaurants/{id}", restaurantId)
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/restaurant-response-schema.json"))
                    .body("id", equalTo(restaurantId))
                    .body("name", equalTo("China in Box"))
                    .body("kitchen.id", equalTo(kitchenId))
                    .body("kitchen.name", equalTo("Chinesa"))
                    .body("active", equalTo(true))
                    .body("delivery_fee", equalTo(8.50f));
        }

        @Test
        @DisplayName("Deve validar headers de resposta corretos")
        void shouldValidateCorrectResponseHeaders() {
            // Given
            String kitchenId = createKitchen("Indiana");
            String restaurantId = createRestaurant("Curry House", kitchenId, false, new BigDecimal("20.00"));

            // When & Then
            given()
                    .when()
                    .get("/v1/restaurants/{id}", restaurantId)
                    .then()
                    .statusCode(200)
                    .header("Content-Type", containsString("application/json"))
                    .body("id", equalTo(restaurantId))
                    .body("active", equalTo(false));
        }

        @Test
        @DisplayName("Deve retornar 404 quando restaurante não existir")
        void shouldReturn404WhenRestaurantNotExists() {
            // Given
            String nonExistentId = UUID.randomUUID().toString();

            // When & Then
            given()
                    .when()
                    .get("/v1/restaurants/{id}", nonExistentId)
                    .then()
                    .statusCode(404)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/problem-details-error-schema.json"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando ID for inválido")
        void shouldReturn400WhenIdIsInvalid() {
            // When & Then
            given()
                    .when()
                    .get("/v1/restaurants/{id}", "invalid-uuid")
                    .then()
                    .statusCode(400)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/problem-details-error-schema.json"));
        }
    }

    @Nested
    @DisplayName("GET /v1/restaurants - Buscar restaurantes com paginação")
    class SearchRestaurantsContract {

        @Test
        @DisplayName("Deve listar restaurantes com paginação e validar page response schema")
        void shouldListRestaurantsWithPaginationAndValidateSchema() {
            // Given - criar múltiplos restaurantes
            String kitchenId1 = createKitchen("Fast Food");
            String kitchenId2 = createKitchen("Saudável");

            createRestaurant("McDonald's", kitchenId1, true, new BigDecimal("5.00"));
            createRestaurant("Burger King", kitchenId1, true, new BigDecimal("6.00"));
            createRestaurant("Mundo Verde", kitchenId2, true, new BigDecimal("12.00"));

            // When & Then
            given()
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/v1/restaurants")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/restaurant-page-response-schema.json"))
                    .body("content", hasSize(greaterThanOrEqualTo(3)))
                    .body("page.number", equalTo(0))
                    .body("page.size", equalTo(10))
                    .body("page.total_elements", greaterThanOrEqualTo(3))
                    .body("page.total_pages", greaterThanOrEqualTo(1));
        }

        @Test
        @DisplayName("Deve validar headers de resposta corretos")
        void shouldValidateCorrectResponseHeaders() {
            // When & Then
            given()
                    .queryParam("page", 0)
                    .queryParam("size", 5)
                    .when()
                    .get("/v1/restaurants")
                    .then()
                    .statusCode(200)
                    .header("Content-Type", containsString("application/json"))
                    .body("page.size", equalTo(5));
        }

        @Test
        @DisplayName("Deve filtrar restaurantes por nome")
        void shouldFilterRestaurantsByName() {
            // Given
            String kitchenId = createKitchen("Pizza");
            createRestaurant("Pizzaria do João", kitchenId, true, new BigDecimal("7.50"));
            createRestaurant("Pizza Hut", kitchenId, true, new BigDecimal("9.00"));

            // When & Then
            given()
                    .queryParam("name", "Pizza")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/v1/restaurants")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/restaurant-page-response-schema.json"));
        }

        @Test
        @DisplayName("Deve filtrar restaurantes por cozinha")
        void shouldFilterRestaurantsByKitchen() {
            // Given
            String kitchenId = createKitchen("Árabe");
            createRestaurant("Habib's", kitchenId, true, new BigDecimal("4.50"));

            // When & Then
            given()
                    .queryParam("kitchenId", kitchenId)
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/v1/restaurants")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/restaurant-page-response-schema.json"));
        }

        @Test
        @DisplayName("Deve filtrar restaurantes por faixa de taxa de entrega")
        void shouldFilterRestaurantsByDeliveryFeeRange() {
            // Given
            String kitchenId = createKitchen("Lanche");
            createRestaurant("Subway", kitchenId, true, new BigDecimal("3.00"));
            createRestaurant("Burger Premium", kitchenId, true, new BigDecimal("25.00"));

            // When & Then
            given()
                    .queryParam("minDeliveryFee", "2.00")
                    .queryParam("maxDeliveryFee", "10.00")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/v1/restaurants")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/restaurant-page-response-schema.json"));
        }
    }

    @Nested
    @DisplayName("PUT /v1/restaurants/{id} - Atualizar restaurante")
    class UpdateRestaurantContract {

        @Test
        @DisplayName("Deve atualizar restaurante com dados válidos e validar response schema")
        void shouldUpdateRestaurantWithValidDataAndValidateSchema() {
            // Given
            String kitchenId = createKitchen("Tailandesa");
            String restaurantId = createRestaurant("Thai Food", kitchenId, true, new BigDecimal("18.00"));
            RestaurantRequest updateRequest = new RestaurantRequest(
                    "Thai Food Premium",
                    UUID.fromString(kitchenId),
                    false,
                    new BigDecimal("22.00")
            );

            // When & Then
            given()
                    .contentType(ContentType.JSON)
                    .body(updateRequest)
                    .when()
                    .put("/v1/restaurants/{id}", restaurantId)
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/restaurant-response-schema.json"))
                    .body("id", equalTo(restaurantId))
                    .body("name", equalTo("Thai Food Premium"))
                    .body("active", equalTo(false))
                    .body("delivery_fee", equalTo(22.00f));
        }

        @Test
        @DisplayName("Deve validar headers de resposta corretos")
        void shouldValidateCorrectResponseHeaders() {
            // Given
            String kitchenId = createKitchen("Coreana");
            String restaurantId = createRestaurant("Korean BBQ", kitchenId, true, new BigDecimal("16.00"));
            RestaurantRequest updateRequest = new RestaurantRequest(
                    "Korean BBQ Premium",
                    UUID.fromString(kitchenId),
                    true,
                    new BigDecimal("20.00")
            );

            // When & Then
            given()
                    .contentType(ContentType.JSON)
                    .body(updateRequest)
                    .when()
                    .put("/v1/restaurants/{id}", restaurantId)
                    .then()
                    .statusCode(200)
                    .header("Content-Type", containsString("application/json"))
                    .body("name", equalTo("Korean BBQ Premium"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando restaurante não existir")
        void shouldReturn404WhenRestaurantNotExists() {
            // Given
            String nonExistentId = UUID.randomUUID().toString();
            String kitchenId = createKitchen("Vegetariana");
            RestaurantRequest updateRequest = new RestaurantRequest(
                    "Veggie House",
                    UUID.fromString(kitchenId),
                    true,
                    new BigDecimal("14.00")
            );

            // When & Then
            given()
                    .contentType(ContentType.JSON)
                    .body(updateRequest)
                    .when()
                    .put("/v1/restaurants/{id}", nonExistentId)
                    .then()
                    .statusCode(404)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/problem-details-error-schema.json"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando dados forem inválidos")
        void shouldReturn400WhenDataIsInvalid() {
            // Given
            String kitchenId = createKitchen("Francesa");
            String restaurantId = createRestaurant("Bistro Français", kitchenId, true, new BigDecimal("30.00"));
            RestaurantRequest invalidRequest = new RestaurantRequest(
                    "Restaurante Atualizado",
                    UUID.fromString(kitchenId),
                    true,
                    new BigDecimal("-10.00") // taxa de entrega negativa
            );

            // When & Then
            given()
                    .contentType(ContentType.JSON)
                    .body(invalidRequest)
                    .when()
                    .put("/v1/restaurants/{id}", restaurantId)
                    .then()
                    .statusCode(400)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/problem-details-error-schema.json"));
        }
    }

    @Nested
    @DisplayName("DELETE /v1/restaurants/{id} - Deletar restaurante")
    class DeleteRestaurantContract {

        @Test
        @DisplayName("Deve deletar restaurante existente com sucesso")
        void shouldDeleteExistingRestaurantSuccessfully() {
            // Given
            String kitchenId = createKitchen("Alemã");
            String restaurantId = createRestaurant("Oktoberfest", kitchenId, true, new BigDecimal("18.50"));

            // When & Then
            given()
                    .when()
                    .delete("/v1/restaurants/{id}", restaurantId)
                    .then()
                    .statusCode(204)
                    .body(equalTo(""));

            // Verificar que o restaurante foi realmente deletado
            given()
                    .when()
                    .get("/v1/restaurants/{id}", restaurantId)
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("Deve validar headers de resposta corretos")
        void shouldValidateCorrectResponseHeaders() {
            // Given
            String kitchenId = createKitchen("Espanhola");
            String restaurantId = createRestaurant("Tapas Bar", kitchenId, true, new BigDecimal("12.75"));

            // When & Then
            given()
                    .when()
                    .delete("/v1/restaurants/{id}", restaurantId)
                    .then()
                    .statusCode(204)
                    .body(equalTo(""));

            // Verificar que não há Content-Type para resposta 204
            // 204 No Content não deve ter corpo nem Content-Type
        }

        @Test
        @DisplayName("Deve retornar 404 quando restaurante não existir")
        void shouldReturn404WhenRestaurantNotExists() {
            // Given
            String nonExistentId = UUID.randomUUID().toString();

            // When & Then
            given()
                    .when()
                    .delete("/v1/restaurants/{id}", nonExistentId)
                    .then()
                    .statusCode(404)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/problem-details-error-schema.json"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando ID for inválido")
        void shouldReturn400WhenIdIsInvalid() {
            // When & Then
            given()
                    .when()
                    .delete("/v1/restaurants/{id}", "invalid-uuid")
                    .then()
                    .statusCode(400)
                    .contentType(ContentType.JSON)
                    .body(matchesJsonSchemaInClasspath("schemas/problem-details-error-schema.json"));
        }
    }

    @Nested
    @DisplayName("Testes de Headers e Content-Type")
    class HeadersAndContentTypeContract {

        @Test
        @DisplayName("Deve sempre retornar Content-Type application/json para responses com corpo")
        void shouldAlwaysReturnJsonContentTypeForResponsesWithBody() {
            // Given
            String kitchenId = createKitchen("Peruana");
            String restaurantId = createRestaurant("Cevicheria", kitchenId, true, new BigDecimal("13.25"));

            // Test GET by ID
            given()
                    .when()
                    .get("/v1/restaurants/{id}", restaurantId)
                    .then()
                    .statusCode(200)
                    .header("Content-Type", containsString("application/json"));

            // Test GET list
            given()
                    .when()
                    .get("/v1/restaurants")
                    .then()
                    .statusCode(200)
                    .header("Content-Type", containsString("application/json"));
        }

        @Test
        @DisplayName("Deve aceitar Content-Type application/json para requests")
        void shouldAcceptJsonContentTypeForRequests() {
            // Given
            String kitchenId = createKitchen("Russa");
            RestaurantRequest request = new RestaurantRequest(
                    "Russian Cuisine",
                    UUID.fromString(kitchenId),
                    true,
                    new BigDecimal("24.00")
            );

            // When & Then
            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/v1/restaurants")
                    .then()
                    .statusCode(201)
                    .header("Content-Type", containsString("application/json"));
        }
    }
}
