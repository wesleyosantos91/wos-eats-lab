package io.github.wesleyosantos91.catalog.cucumber.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.github.wesleyosantos91.catalog.cucumber.utils.FeatureUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class RestaurantStep {

    private final TestRestTemplate restTemplate;

    public RestaurantStep(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @LocalServerPort
    private int randomServerPort;

    private ResponseEntity<String> response;
    private UUID createdRestaurantId;
    private UUID createdKitchenId;
    private String baseUrl;

    private UUID criarCozinhaParaTeste(String nomeCozinha) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String kitchenUrl = FeatureUtils.getHost(randomServerPort) + "/v1/kitchens";
        String requestBody = String.format("{\"name\": \"%s\"}", nomeCozinha);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> kitchenResponse = restTemplate.postForEntity(kitchenUrl, request, String.class);
        
        if (kitchenResponse.getStatusCode().is2xxSuccessful() && kitchenResponse.getBody() != null) {
            return UUID.fromString(JsonPath.parse(kitchenResponse.getBody()).read("$.id"));
        }
        
        throw new RuntimeException("Falha ao criar cozinha para teste: " + nomeCozinha);
    }

    @Dado("que o serviço de catálogo de restaurantes está disponível")
    public void queOServicoDeCatalogoDeRestaurantesEstaDisponivel() {
        baseUrl = FeatureUtils.getHost(randomServerPort) + "/v1/restaurants";
        ResponseEntity<String> healthResponse = restTemplate.getForEntity(
                FeatureUtils.getHost(randomServerPort) + "/actuator/health",
                String.class
        );
        assertEquals(HttpStatus.OK, healthResponse.getStatusCode());
    }

    @Quando("eu crio um restaurante com o nome {string}")
    public void euCrioUmRestauranteComONome(String nome) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UUID kitchenId = criarCozinhaParaTeste("Italiana");
        
        String requestBody = String.format("{\"name\": \"%s\", \"kitchenId\": \"%s\", \"deliveryFee\": 5.99}", 
                nome, kitchenId);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        response = restTemplate.postForEntity(baseUrl, request, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            createdRestaurantId = UUID.fromString(JsonPath.parse(response.getBody()).read("$.id"));
        }
    }

    @Então("o restaurante deve ser criado com sucesso")
    public void oRestauranteDeveSerCriadoComSucesso() {
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(createdRestaurantId);
    }

    @E("o status code do restaurante deve ser {int}")
    public void oStatusCodeDoRestauranteDeveSer(int expectedStatusCode) {
        assertEquals(HttpStatusCode.valueOf(expectedStatusCode), response.getStatusCode());
    }

    @E("a resposta do restaurante deve conter o nome {string}")
    public void aRespostaDoRestauranteDeveConterONome(String expectedName) {
        assertNotNull(response.getBody());
        String actualName = JsonPath.parse(response.getBody()).read("$.name");
        assertEquals(expectedName, actualName);
    }

    @Dado("que existe um restaurante cadastrado com o nome {string}")
    public void queExisteUmRestauranteCadastradoComONome(String nome) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        createdKitchenId = criarCozinhaParaTeste("Cozinha Teste");
        
        String requestBody = String.format("{\"name\": \"%s\", \"kitchenId\": \"%s\", \"deliveryFee\": 5.99}", 
                nome, createdKitchenId);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> createResponse = restTemplate.postForEntity(baseUrl, request, String.class);

        if (createResponse.getStatusCode().is2xxSuccessful() && createResponse.getBody() != null) {
            createdRestaurantId = UUID.fromString(JsonPath.parse(createResponse.getBody()).read("$.id"));
        }
    }

    @Quando("eu busco o restaurante pelo ID")
    public void euBuscoORestaurantePeloID() {
        response = restTemplate.getForEntity(baseUrl + "/" + createdRestaurantId, String.class);
    }

    @Dado("que existem os seguintes restaurantes cadastrados:")
    public void queExistemOsSeguintesRestaurantesCadastrados(DataTable dataTable) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String nome = row.get("nome");
            UUID kitchenId = criarCozinhaParaTeste("Cozinha para " + nome);
            
            String requestBody = String.format("{\"name\": \"%s\", \"kitchenId\": \"%s\", \"deliveryFee\": 5.99}", 
                    nome, kitchenId);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            restTemplate.postForEntity(baseUrl, request, String.class);
        }
    }

    @Quando("eu listo todos os restaurantes")
    public void euListoTodosOsRestaurantes() {
        response = restTemplate.getForEntity(baseUrl, String.class);
    }

    @E("a resposta deve conter pelo menos {int} restaurantes")
    public void aRespostaDeveConterPeloMenosRestaurantes(int expectedMinCount) {
        assertNotNull(response.getBody());
        Integer totalElements = JsonPath.parse(response.getBody()).read("$.page.totalElements");
        assertTrue(totalElements >= expectedMinCount,
                "Expected at least " + expectedMinCount + " restaurants, but found " + totalElements);
    }

    @Quando("eu atualizo o nome do restaurante para {string}")
    public void euAtualizoONomeDoRestaurantePara(String novoNome) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"name\": \"%s\", \"kitchenId\": \"%s\", \"deliveryFee\": 7.99, \"active\": true}", 
                novoNome, createdKitchenId);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        response = restTemplate.exchange(
                baseUrl + "/" + createdRestaurantId,
                HttpMethod.PUT,
                request,
                String.class
        );
    }

    @Quando("eu deleto o restaurante")
    public void euDeletoORestaurante() {
        response = restTemplate.exchange(
                baseUrl + "/" + createdRestaurantId,
                HttpMethod.DELETE,
                null,
                String.class
        );
    }

    @Quando("eu tento criar um restaurante com o nome {string}")
    public void euTentoCriarUmRestauranteComONome(String nome) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UUID kitchenId = criarCozinhaParaTeste("Cozinha Teste Duplicado");
        
        String requestBody = String.format("{\"name\": \"%s\", \"kitchenId\": \"%s\", \"deliveryFee\": 5.99}", 
                nome, kitchenId);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        response = restTemplate.postForEntity(baseUrl, request, String.class);
    }

    @Quando("eu busco um restaurante com ID inexistente")
    public void euBuscoUmRestauranteComIDInexistente() {
        UUID nonExistentId = UUID.randomUUID();
        response = restTemplate.getForEntity(baseUrl + "/" + nonExistentId, String.class);
    }
}