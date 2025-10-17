package io.github.wesleyosantos91.catalog.api.v1.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.wesleyosantos91.catalog.TestcontainersConfiguration;
import io.github.wesleyosantos91.catalog.api.v1.request.KitchenRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.RestaurantRequest;
import io.github.wesleyosantos91.catalog.api.v1.response.KitchenResponse;
import io.github.wesleyosantos91.catalog.api.v1.response.RestaurantResponse;
import io.github.wesleyosantos91.catalog.domain.repository.KitchenRepository;
import io.github.wesleyosantos91.catalog.domain.repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("integration")
@DisplayName("RestaurantController - Integration Tests")
class RestaurantControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private KitchenRepository kitchenRepository;

    @LocalServerPort
    private int port;

    private String baseUrl;
    private String kitchenBaseUrl;
    private UUID kitchenId;

    @BeforeEach
    void setUp() {
        restaurantRepository.deleteAll();
        kitchenRepository.deleteAll();

        baseUrl = "http://localhost:" + port + "/v1/restaurants";
        kitchenBaseUrl = "http://localhost:" + port + "/v1/kitchens";
        kitchenId = criarCozinha("Italiana");
    }

    @AfterEach
    void tearDown() {
        restaurantRepository.deleteAll();
        kitchenRepository.deleteAll();
    }

    private UUID criarCozinha(String name) {
        KitchenRequest request = new KitchenRequest(name);
        HttpEntity<KitchenRequest> entity = new HttpEntity<>(request, new HttpHeaders());
        ResponseEntity<KitchenResponse> response = restTemplate.postForEntity(
                kitchenBaseUrl,
                entity,
                KitchenResponse.class
        );
        return response.getBody().id();
    }

    private UUID criarRestaurante(String name, UUID kitchenId, Boolean active, BigDecimal deliveryFee) {
        RestaurantRequest request = new RestaurantRequest(name, kitchenId, active, deliveryFee);
        HttpEntity<RestaurantRequest> entity = new HttpEntity<>(request, new HttpHeaders());
        ResponseEntity<RestaurantResponse> response = restTemplate.postForEntity(
                baseUrl,
                entity,
                RestaurantResponse.class
        );
        return response.getBody().id();
    }

    @Nested
    @DisplayName("POST /v1/restaurants - Criar restaurante")
    class CreateRestaurant {

        @Test
        @DisplayName("Deve criar um restaurante com sucesso - Integração completa")
        void deveCriarRestauranteComSucesso() {
            RestaurantRequest request = new RestaurantRequest(
                    "Restaurante Italiano",
                    kitchenId,
                    true,
                    new BigDecimal("10.00")
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RestaurantRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<RestaurantResponse> response = restTemplate.postForEntity(
                    baseUrl,
                    entity,
                    RestaurantResponse.class
            );

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Restaurante Italiano", response.getBody().name());
            assertEquals(kitchenId, response.getBody().kitchen().id());
            assertEquals(true, response.getBody().active());
            assertEquals(new BigDecimal("10.00"), response.getBody().deliveryFee());
            assertNotNull(response.getBody().id());

            // Verifica se foi persistido no banco
            var savedRestaurant = restaurantRepository.findById(response.getBody().id());
            assertTrue(savedRestaurant.isPresent());
            assertEquals("Restaurante Italiano", savedRestaurant.get().getName());
            assertEquals(kitchenId, savedRestaurant.get().getKitchen().getId());
        }

        @Test
        @DisplayName("Deve retornar 409 ao tentar criar restaurante com nome duplicado")
        void deveRetornar409QuandoNomeDuplicado() {
            RestaurantRequest request = new RestaurantRequest(
                    "Restaurante Duplicado",
                    kitchenId,
                    true,
                    new BigDecimal("15.00")
            );

            // Cria primeiro restaurante
            HttpEntity<RestaurantRequest> entity = new HttpEntity<>(request, new HttpHeaders());
            restTemplate.postForEntity(baseUrl, entity, RestaurantResponse.class);

            // Tenta criar segundo restaurante com mesmo nome
            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl,
                    entity,
                    String.class
            );

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }

        @Test
        @DisplayName("Deve retornar 404 quando cozinha não existir")
        void deveRetornar404QuandoCozinhaNaoExistir() {
            UUID nonExistentKitchenId = UUID.randomUUID();
            RestaurantRequest request = new RestaurantRequest(
                    "Restaurante Teste",
                    nonExistentKitchenId,
                    true,
                    new BigDecimal("12.00")
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RestaurantRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl,
                    entity,
                    String.class
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Deve retornar 400 quando request body for inválido")
        void deveRetornar400QuandoRequestInvalido() {
            RestaurantRequest invalidRequest = new RestaurantRequest(
                    null, // nome obrigatório
                    kitchenId,
                    true,
                    new BigDecimal("10.00")
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RestaurantRequest> entity = new HttpEntity<>(invalidRequest, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl,
                    entity,
                    String.class
            );

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("GET /v1/restaurants/{id} - Buscar restaurante por ID")
    class GetRestaurantById {

        @Test
        @DisplayName("Deve buscar um restaurante por ID com sucesso")
        void deveBuscarRestaurantePorId() {
            // Cria restaurante primeiro
            UUID restaurantId = criarRestaurante("Restaurante Busca", kitchenId, true, new BigDecimal("8.50"));

            // Busca o restaurante criado
            ResponseEntity<RestaurantResponse> response = restTemplate.getForEntity(
                    baseUrl + "/" + restaurantId,
                    RestaurantResponse.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(restaurantId, response.getBody().id());
            assertEquals("Restaurante Busca", response.getBody().name());
            assertEquals(kitchenId, response.getBody().kitchen().id());
            assertEquals("Italiana", response.getBody().kitchen().name());
            assertEquals(true, response.getBody().active());
            assertEquals(new BigDecimal("8.50"), response.getBody().deliveryFee());
        }

        @Test
        @DisplayName("Deve retornar 404 quando restaurante não for encontrado")
        void deveRetornar404QuandoRestauranteNaoEncontrado() {
            UUID nonExistentId = UUID.randomUUID();

            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "/" + nonExistentId,
                    String.class
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("GET /v1/restaurants - Listar restaurantes")
    class SearchRestaurants {

        @Test
        @DisplayName("Deve listar todos os restaurantes com paginação")
        void deveListarTodosRestaurantes() {
            // Cria alguns restaurantes
            criarRestaurante("Restaurante A", kitchenId, true, new BigDecimal("10.00"));
            criarRestaurante("Restaurante B", kitchenId, false, new BigDecimal("12.00"));
            criarRestaurante("Restaurante C", kitchenId, true, new BigDecimal("8.00"));

            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "?page=0&size=10",
                    String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().contains("\"content\""));
        }

        @Test
        @DisplayName("Deve filtrar restaurantes por nome")
        void deveFiltrarRestaurantesPorNome() {
            criarRestaurante("Pizzaria Bella", kitchenId, true, new BigDecimal("15.00"));
            criarRestaurante("Restaurante Outro", kitchenId, true, new BigDecimal("10.00"));

            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "?name=Pizzaria&page=0&size=10",
                    String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().contains("Pizzaria Bella"));
        }

        @Test
        @DisplayName("Deve filtrar restaurantes por status ativo")
        void deveFiltrarRestaurantesPorStatusAtivo() {
            criarRestaurante("Restaurante Ativo", kitchenId, true, new BigDecimal("10.00"));
            criarRestaurante("Restaurante Inativo", kitchenId, false, new BigDecimal("12.00"));

            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "?active=true&page=0&size=10",
                    String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().contains("Restaurante Ativo"));
        }

        @Test
        @DisplayName("Deve filtrar restaurantes por cozinha")
        void deveFiltrarRestaurantesPorCozinha() {
            UUID outraKitchenId = criarCozinha("Japonesa");
            criarRestaurante("Sushi Bar", outraKitchenId, true, new BigDecimal("20.00"));
            criarRestaurante("Cantina Italiana", kitchenId, true, new BigDecimal("15.00"));

            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "?kitchenId=" + outraKitchenId + "&page=0&size=10",
                    String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().contains("Sushi Bar"));
        }

        @Test
        @DisplayName("Deve filtrar restaurantes por faixa de taxa de entrega")
        void deveFiltrarRestaurantesPorFaixaTaxaEntrega() {
            criarRestaurante("Restaurante Barato", kitchenId, true, new BigDecimal("5.00"));
            criarRestaurante("Restaurante Médio", kitchenId, true, new BigDecimal("12.00"));
            criarRestaurante("Restaurante Caro", kitchenId, true, new BigDecimal("25.00"));

            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "?minDeliveryFee=10.00&maxDeliveryFee=20.00&page=0&size=10",
                    String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().contains("Restaurante Médio"));
        }
    }

    @Nested
    @DisplayName("PUT /v1/restaurants/{id} - Atualizar restaurante")
    class UpdateRestaurant {

        @Test
        @DisplayName("Deve atualizar um restaurante com sucesso")
        void deveAtualizarRestauranteComSucesso() {
            UUID restaurantId = criarRestaurante("Restaurante Original", kitchenId, true, new BigDecimal("10.00"));

            RestaurantRequest updateRequest = new RestaurantRequest(
                    "Restaurante Atualizado",
                    kitchenId,
                    false,
                    new BigDecimal("15.00")
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RestaurantRequest> entity = new HttpEntity<>(updateRequest, headers);

            ResponseEntity<RestaurantResponse> response = restTemplate.exchange(
                    baseUrl + "/" + restaurantId,
                    HttpMethod.PUT,
                    entity,
                    RestaurantResponse.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(restaurantId, response.getBody().id());
            assertEquals("Restaurante Atualizado", response.getBody().name());
            assertEquals(false, response.getBody().active());
            assertEquals(new BigDecimal("15.00"), response.getBody().deliveryFee());

            var updatedRestaurant = restaurantRepository.findById(restaurantId);
            assertTrue(updatedRestaurant.isPresent());
            assertEquals("Restaurante Atualizado", updatedRestaurant.get().getName());
            assertEquals(false, updatedRestaurant.get().getActive());
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar restaurante inexistente")
        void deveRetornar404AoAtualizarRestauranteInexistente() {
            UUID nonExistentId = UUID.randomUUID();
            RestaurantRequest updateRequest = new RestaurantRequest(
                    "Restaurante Teste",
                    kitchenId,
                    true,
                    new BigDecimal("10.00")
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RestaurantRequest> entity = new HttpEntity<>(updateRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/" + nonExistentId,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Deve retornar 409 quando novo nome já existir")
        void deveRetornar409QuandoNovoNomeJaExistir() {
            UUID restaurantId1 = criarRestaurante("Restaurante Um", kitchenId, true, new BigDecimal("10.00"));
            criarRestaurante("Restaurante Dois", kitchenId, true, new BigDecimal("12.00"));

            RestaurantRequest updateRequest = new RestaurantRequest(
                    "Restaurante Dois",
                    kitchenId,
                    true,
                    new BigDecimal("10.00")
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RestaurantRequest> entity = new HttpEntity<>(updateRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/" + restaurantId1,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("DELETE /v1/restaurants/{id} - Deletar restaurante")
    class DeleteRestaurant {

        @Test
        @DisplayName("Deve deletar um restaurante com sucesso")
        void deveDeletarRestauranteComSucesso() {
            UUID restaurantId = criarRestaurante("Restaurante Delete", kitchenId, true, new BigDecimal("10.00"));

            assertTrue(restaurantRepository.findById(restaurantId).isPresent());

            ResponseEntity<Void> response = restTemplate.exchange(
                    baseUrl + "/" + restaurantId,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

            assertTrue(restaurantRepository.findById(restaurantId).isEmpty());
        }

        @Test
        @DisplayName("Deve retornar 404 ao deletar restaurante inexistente")
        void deveRetornar404AoDeletarRestauranteInexistente() {
            UUID nonExistentId = UUID.randomUUID();

            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/" + nonExistentId,
                    HttpMethod.DELETE,
                    null,
                    String.class
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }
}
