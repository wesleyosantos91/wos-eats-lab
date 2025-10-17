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

public class KitchenStep {


    private final TestRestTemplate restTemplate;

    public KitchenStep(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @LocalServerPort
    private int randomServerPort;

    private ResponseEntity<String> response;
    private UUID createdKitchenId;
    private String baseUrl;

    @Dado("que o serviço de catálogo está disponível")
    public void queOServicoDeCatalogoEstaDisponivel() {
        baseUrl = FeatureUtils.getHost(randomServerPort) + "/v1/kitchens";
        ResponseEntity<String> healthResponse = restTemplate.getForEntity(
                FeatureUtils.getHost(randomServerPort) + "/actuator/health",
                String.class
        );
        assertEquals(HttpStatus.OK, healthResponse.getStatusCode());
    }

    @Quando("eu crio uma cozinha com o nome {string}")
    public void euCrioUmaCozinhaComONome(String nome) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"name\": \"%s\"}", nome);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        response = restTemplate.postForEntity(baseUrl, request, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            createdKitchenId = UUID.fromString(JsonPath.parse(response.getBody()).read("$.id"));
        }
    }

    @Então("a cozinha deve ser criada com sucesso")
    public void aCozinhaDeveSerCriadaComSucesso() {
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(createdKitchenId);
    }

    @E("o status code deve ser {int}")
    public void oStatusCodeDeveSer(int expectedStatusCode) {
        assertEquals(HttpStatusCode.valueOf(expectedStatusCode), response.getStatusCode());
    }

    @E("a resposta deve conter o nome {string}")
    public void aRespostaDeveConterONome(String expectedName) {
        assertNotNull(response.getBody());
        String actualName = JsonPath.parse(response.getBody()).read("$.name");
        assertEquals(expectedName, actualName);
    }

    @Dado("que existe uma cozinha cadastrada com o nome {string}")
    public void queExisteUmaCozinhaCadastradaComONome(String nome) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"name\": \"%s\"}", nome);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> createResponse = restTemplate.postForEntity(baseUrl, request, String.class);

        if (createResponse.getStatusCode().is2xxSuccessful() && createResponse.getBody() != null) {
            createdKitchenId = UUID.fromString(JsonPath.parse(createResponse.getBody()).read("$.id"));
        }
    }

    @Quando("eu busco a cozinha pelo ID")
    public void euBuscoACozinhaPeloID() {
        response = restTemplate.getForEntity(baseUrl + "/" + createdKitchenId, String.class);
    }

    @Dado("que existem as seguintes cozinhas cadastradas:")
    public void queExistemAsSeguintesCozinhasCadastradas(DataTable dataTable) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String nome = row.get("nome");
            String requestBody = String.format("{\"name\": \"%s\"}", nome);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            restTemplate.postForEntity(baseUrl, request, String.class);
        }
    }

    @Quando("eu listo todas as cozinhas")
    public void euListoTodasAsCozinhas() {
        response = restTemplate.getForEntity(baseUrl, String.class);
    }

    @E("a resposta deve conter pelo menos {int} cozinhas")
    public void aRespostaDeveConterPeloMenosCozinhas(int expectedMinCount) {
        assertNotNull(response.getBody());
        Integer totalElements = JsonPath.parse(response.getBody()).read("$.page.totalElements");
        assertTrue(totalElements >= expectedMinCount,
                "Expected at least " + expectedMinCount + " kitchens, but found " + totalElements);
    }

    @Quando("eu atualizo o nome da cozinha para {string}")
    public void euAtualizoONomeDaCozinhaPara(String novoNome) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"name\": \"%s\"}", novoNome);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        response = restTemplate.exchange(
                baseUrl + "/" + createdKitchenId,
                HttpMethod.PUT,
                request,
                String.class
        );
    }

    @Quando("eu deleto a cozinha")
    public void euDeletoACozinha() {
        response = restTemplate.exchange(
                baseUrl + "/" + createdKitchenId,
                HttpMethod.DELETE,
                null,
                String.class
        );
    }

    @Quando("eu tento criar uma cozinha com o nome {string}")
    public void euTentoCriarUmaCozinhaComONome(String nome) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"name\": \"%s\"}", nome);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        response = restTemplate.postForEntity(baseUrl, request, String.class);
    }

    @Quando("eu busco uma cozinha com ID inexistente")
    public void euBuscoUmaCozinhaComIDInexistente() {
        UUID nonExistentId = UUID.randomUUID();
        response = restTemplate.getForEntity(baseUrl + "/" + nonExistentId, String.class);
    }
}
