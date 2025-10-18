package io.github.wesleyosantos91.catalog.api.v1.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.awspring.cloud.s3.S3Template;
import io.github.wesleyosantos91.catalog.TestcontainersConfiguration;
import io.github.wesleyosantos91.catalog.api.v1.request.KitchenRequest;
import io.github.wesleyosantos91.catalog.api.v1.response.KitchenResponse;
import io.github.wesleyosantos91.catalog.api.v1.response.ProductResponse;
import io.github.wesleyosantos91.catalog.domain.repository.ProductRepository;
import io.github.wesleyosantos91.catalog.domain.repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("integration")
@DisplayName("ProductController - Integration Tests (image)")
public class ProductControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private S3Template template;

    @Value("${app.s3-bucket-name}")
    private String bucketName;

    @LocalServerPort
    private int port;

    private String baseUrl;
    private String kitchenBaseUrl;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        restaurantRepository.deleteAll();

        baseUrl = "http://localhost:" + port + "/v1/products";
        kitchenBaseUrl = "http://localhost:" + port + "/v1/kitchens";

        template.createBucket(bucketName);

    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        restaurantRepository.deleteAll();
        template.deleteBucket(bucketName);
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
        var request = new io.github.wesleyosantos91.catalog.api.v1.request.RestaurantRequest(name, kitchenId, active, deliveryFee);
        HttpEntity<io.github.wesleyosantos91.catalog.api.v1.request.RestaurantRequest> entity = new HttpEntity<>(request, new HttpHeaders());
        ResponseEntity<io.github.wesleyosantos91.catalog.api.v1.response.RestaurantResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/restaurants",
                entity,
                io.github.wesleyosantos91.catalog.api.v1.response.RestaurantResponse.class
        );
        return response.getBody().id();
    }

    @Test
    @DisplayName("Deve criar produto com imagem, baixar, atualizar e deletar a imagem")
    void deveCriarAtualizarDeletarImagem() throws Exception {
        UUID kitchenId = criarCozinha("Italiana");
        UUID restaurantId = criarRestaurante("Restaurante Img", kitchenId, true, new BigDecimal("5.00"));

        // Criar produto com imagem
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("restaurant_id", restaurantId.toString());
        body.add("name", "Produto Img");
        body.add("description", "Descricao");
        body.add("price", "10.00");
        body.add("active", "true");
        ClassPathResource image = new ClassPathResource("image/eclipse.png");
        body.add("image", image);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ProductResponse> createResponse = restTemplate.postForEntity(baseUrl, requestEntity, ProductResponse.class);
        assertEquals(201, createResponse.getStatusCodeValue());
        assertNotNull(createResponse.getBody());
        UUID productId = createResponse.getBody().id();

        // GET imagem
        ResponseEntity<byte[]> imageResponse = restTemplate.getForEntity(baseUrl + "/" + productId + "/image", byte[].class);
        assertEquals(200, imageResponse.getStatusCodeValue());
        assertNotNull(imageResponse.getBody());
        assertTrue(imageResponse.getHeaders().getContentType().toString().startsWith("image/"));

        // Atualizar imagem
        MultiValueMap<String, Object> updateBody = new LinkedMultiValueMap<>();
        updateBody.add("restaurant_id", restaurantId.toString());
        updateBody.add("name", "Produto Img Atualizado");
        updateBody.add("description", "Descricao");
        updateBody.add("price", "12.00");
        updateBody.add("active", "true");
        ClassPathResource image2 = new ClassPathResource("image/java.png");
        updateBody.add("image", image2);

        HttpEntity<MultiValueMap<String, Object>> updateEntity = new HttpEntity<>(updateBody, headers);
        ResponseEntity<ProductResponse> updateResponse = restTemplate.exchange(baseUrl + "/" + productId, HttpMethod.PUT, updateEntity, ProductResponse.class);
        assertEquals(200, updateResponse.getStatusCodeValue());

        // Deletar imagem
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(baseUrl + "/" + productId + "/image", HttpMethod.DELETE, null, Void.class);
        assertEquals(200, deleteResponse.getStatusCodeValue());

        // GET imagem deve retornar 404
        ResponseEntity<String> imageAfterDelete = restTemplate.getForEntity(baseUrl + "/" + productId + "/image", String.class);
        assertEquals(404, imageAfterDelete.getStatusCodeValue());
    }
}
