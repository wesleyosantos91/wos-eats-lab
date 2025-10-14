package io.github.wesleyosantos91.catalog.api.v1.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wesleyosantos91.catalog.TestcontainersConfiguration;
import io.github.wesleyosantos91.catalog.api.v1.request.KitchenRequest;
import io.github.wesleyosantos91.catalog.api.v1.response.KitchenResponse;
import io.github.wesleyosantos91.catalog.domain.repository.KitchenRepository;
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

/**
 * Testes de integração para KitchenController.
 * Usa @SpringBootTest com porta aleatória e Testcontainers para banco PostgreSQL.
 * Testa o fluxo completo incluindo controller, service, repository e banco de dados.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("integration")
@DisplayName("KitchenController - Integration Tests")
class KitchenControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KitchenRepository kitchenRepository;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/v1/kitchens";
    }

    @AfterEach
    void tearDown() {
        // Limpa o banco após cada teste para garantir isolamento
        kitchenRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /v1/kitchens - Criar cozinha")
    class CreateKitchen {

        @Test
        @DisplayName("Deve criar uma cozinha com sucesso - Integração completa")
        void deveCriarCozinhaComSucesso() {
            KitchenRequest request = new KitchenRequest("Italiana");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<KitchenRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<KitchenResponse> response = restTemplate.postForEntity(
                    baseUrl,
                    entity,
                    KitchenResponse.class
            );

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Italiana", response.getBody().name());
            assertNotNull(response.getBody().id());

            // Verifica se foi persistido no banco
            var savedKitchen = kitchenRepository.findById(response.getBody().id());
            assertEquals(true, savedKitchen.isPresent());
            assertEquals("Italiana", savedKitchen.get().getName());
        }

        @Test
        @DisplayName("Deve retornar 409 ao tentar criar cozinha com nome duplicado")
        void deveRetornar409QuandoNomeDuplicado() {
            // Cria primeira cozinha
            KitchenRequest request = new KitchenRequest("Japonesa");
            HttpEntity<KitchenRequest> entity = new HttpEntity<>(request, new HttpHeaders());
            restTemplate.postForEntity(baseUrl, entity, KitchenResponse.class);

            // Tenta criar segunda cozinha com mesmo nome
            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl,
                    entity,
                    String.class
            );

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }

        @Test
        @DisplayName("Deve retornar 400 quando request body for inválido")
        void deveRetornar400QuandoRequestInvalido() {
            KitchenRequest invalidRequest = new KitchenRequest(null);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<KitchenRequest> entity = new HttpEntity<>(invalidRequest, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl,
                    entity,
                    String.class
            );

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("GET /v1/kitchens/{id} - Buscar cozinha por ID")
    class GetKitchenById {

        @Test
        @DisplayName("Deve buscar uma cozinha por ID com sucesso")
        void deveBuscarCozinhaPorId() {
            // Cria cozinha primeiro
            KitchenRequest request = new KitchenRequest("Mexicana");
            HttpEntity<KitchenRequest> entity = new HttpEntity<>(request, new HttpHeaders());
            ResponseEntity<KitchenResponse> createResponse = restTemplate.postForEntity(
                    baseUrl,
                    entity,
                    KitchenResponse.class
            );

            UUID kitchenId = createResponse.getBody().id();

            // Busca a cozinha criada
            ResponseEntity<KitchenResponse> response = restTemplate.getForEntity(
                    baseUrl + "/" + kitchenId,
                    KitchenResponse.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(kitchenId, response.getBody().id());
            assertEquals("Mexicana", response.getBody().name());
        }

        @Test
        @DisplayName("Deve retornar 404 quando cozinha não for encontrada")
        void deveRetornar404QuandoCozinhaNaoEncontrada() {
            UUID nonExistentId = UUID.randomUUID();

            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "/" + nonExistentId,
                    String.class
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("GET /v1/kitchens - Listar cozinhas")
    class SearchKitchens {

        @Test
        @DisplayName("Deve listar todas as cozinhas com paginação")
        void deveListarTodasCozinhas() {
            // Cria algumas cozinhas
            criarCozinha("Brasileira");
            criarCozinha("Tailandesa");
            criarCozinha("Indiana");

            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "?page=0&size=10",
                    String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        @DisplayName("Deve filtrar cozinhas por nome")
        void deveFiltrarCozinhasPorNome() {
            criarCozinha("Italiana");
            criarCozinha("Japonesa");

            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "?name=Italiana&page=0&size=10",
                    String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        private void criarCozinha(String nome) {
            KitchenRequest request = new KitchenRequest(nome);
            HttpEntity<KitchenRequest> entity = new HttpEntity<>(request, new HttpHeaders());
            restTemplate.postForEntity(baseUrl, entity, KitchenResponse.class);
        }
    }

    @Nested
    @DisplayName("PUT /v1/kitchens/{id} - Atualizar cozinha")
    class UpdateKitchen {

        @Test
        @DisplayName("Deve atualizar uma cozinha com sucesso")
        void deveAtualizarCozinhaComSucesso() {
            // Cria cozinha
            KitchenRequest createRequest = new KitchenRequest("Chinesa");
            HttpEntity<KitchenRequest> createEntity = new HttpEntity<>(createRequest, new HttpHeaders());
            ResponseEntity<KitchenResponse> createResponse = restTemplate.postForEntity(
                    baseUrl,
                    createEntity,
                    KitchenResponse.class
            );

            UUID kitchenId = createResponse.getBody().id();

            // Atualiza a cozinha
            KitchenRequest updateRequest = new KitchenRequest("Chinesa Tradicional");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<KitchenRequest> updateEntity = new HttpEntity<>(updateRequest, headers);

            ResponseEntity<KitchenResponse> response = restTemplate.exchange(
                    baseUrl + "/" + kitchenId,
                    HttpMethod.PUT,
                    updateEntity,
                    KitchenResponse.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Chinesa Tradicional", response.getBody().name());

            // Verifica se foi atualizado no banco
            var updatedKitchen = kitchenRepository.findById(kitchenId);
            assertEquals(true, updatedKitchen.isPresent());
            assertEquals("Chinesa Tradicional", updatedKitchen.get().getName());
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar cozinha inexistente")
        void deveRetornar404AoAtualizarCozinhaInexistente() {
            UUID nonExistentId = UUID.randomUUID();
            KitchenRequest updateRequest = new KitchenRequest("Qualquer Nome");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<KitchenRequest> entity = new HttpEntity<>(updateRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/" + nonExistentId,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("DELETE /v1/kitchens/{id} - Deletar cozinha")
    class DeleteKitchen {

        @Test
        @DisplayName("Deve deletar uma cozinha com sucesso")
        void deveDeletarCozinhaComSucesso() {
            // Cria cozinha
            KitchenRequest request = new KitchenRequest("Francesa");
            HttpEntity<KitchenRequest> entity = new HttpEntity<>(request, new HttpHeaders());
            ResponseEntity<KitchenResponse> createResponse = restTemplate.postForEntity(
                    baseUrl,
                    entity,
                    KitchenResponse.class
            );

            UUID kitchenId = createResponse.getBody().id();

            // Deleta a cozinha
            ResponseEntity<Void> response = restTemplate.exchange(
                    baseUrl + "/" + kitchenId,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

            // Verifica se foi deletado do banco
            var deletedKitchen = kitchenRepository.findById(kitchenId);
            assertEquals(false, deletedKitchen.isPresent());
        }

        @Test
        @DisplayName("Deve retornar 404 ao deletar cozinha inexistente")
        void deveRetornar404AoDeletarCozinhaInexistente() {
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

