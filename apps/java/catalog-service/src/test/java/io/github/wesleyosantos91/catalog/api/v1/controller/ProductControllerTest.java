package io.github.wesleyosantos91.catalog.api.v1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.wesleyosantos91.catalog.core.mapper.ProductMapper;
import io.github.wesleyosantos91.catalog.core.port.in.product.ProductServicePort;
import io.github.wesleyosantos91.catalog.domain.entity.ProductEntity;
import io.github.wesleyosantos91.catalog.domain.entity.RestaurantEntity;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
import io.github.wesleyosantos91.catalog.domain.model.ProductModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController - Unit Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductServicePort productService;

    private ProductEntity productEntity;
    private UUID productId;
    private UUID restaurantId;
    

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();

        productEntity = new ProductEntity();
        productEntity.setId(productId);
        productEntity.setName("Pizza Margherita");
        productEntity.setDescription("Traditional pizza");
        productEntity.setPrice(new BigDecimal("25.50"));
        productEntity.setActive(true);
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(restaurantId);
        productEntity.setRestaurant(restaurantEntity);

    }

    @Nested
    @DisplayName("POST /v1/products - Criar produto")
    class CreateProduct {

    @Test
    @DisplayName("Deve criar um produto com sucesso e retornar 201")
        void shouldCreateProduct() throws Exception {
            when(productService.create(any(ProductModel.class), any()))
                    .thenReturn(ProductMapper.MAPPER.toModel(productEntity));

            MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[]{1, 2, 3});

            mockMvc.perform(multipart("/v1/products")
                            .file(image)
                            .param("restaurant_id", restaurantId.toString())
                            .param("name", "Pizza Margherita")
                            .param("description", "Traditional pizza")
                            .param("price", "25.50")
                            .param("active", "true"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(productId.toString()))
                    .andExpect(jsonPath("$.name").value("Pizza Margherita"))
                    .andExpect(jsonPath("$.description").value("Traditional pizza"))
                    .andExpect(jsonPath("$.price").value(25.50))
                    .andExpect(jsonPath("$.active").value(true));

            verify(productService, times(1)).create(any(ProductModel.class), any());
        }

    @Test
    @DisplayName("Deve retornar 404 quando restaurante não for encontrado")
        void shouldReturn404WhenRestaurantNotFound() throws Exception {
            when(productService.create(any(ProductModel.class), any()))
                    .thenThrow(new ResourceNotFoundException("Restaurant", restaurantId.toString()));

            mockMvc.perform(multipart("/v1/products")
                            .param("restaurant_id", restaurantId.toString())
                            .param("name", "Pizza Margherita")
                            .param("description", "Traditional pizza")
                            .param("price", "25.50")
                            .param("active", "true"))
                    .andExpect(status().isNotFound());

            verify(productService, times(1)).create(any(ProductModel.class), any());
        }
    }

    @Nested
    @DisplayName("GET /v1/products/{id} - Buscar produto por ID")
    class GetProductById {

    @Test
    @DisplayName("Deve retornar produto pelo ID")
        void shouldReturnProductById() throws Exception {
            when(productService.findById(productId)).thenReturn(ProductMapper.MAPPER.toModel(productEntity));

            mockMvc.perform(get("/v1/products/{id}", productId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(productId.toString()))
                    .andExpect(jsonPath("$.name").value("Pizza Margherita"));

            verify(productService, times(1)).findById(productId);
        }

    @Test
    @DisplayName("Deve retornar 404 quando produto não for encontrado")
        void shouldReturn404WhenNotFound() throws Exception {
            when(productService.findById(productId))
                    .thenThrow(new ResourceNotFoundException("Product", productId.toString()));

            mockMvc.perform(get("/v1/products/{id}", productId))
                    .andExpect(status().isNotFound());

            verify(productService, times(1)).findById(productId);
        }
    }

    @Nested
    @DisplayName("GET /v1/products - Listar produtos")
    class SearchProducts {

    @Test
    @DisplayName("Deve retornar lista paginada de produtos")
        void shouldReturnPagedList() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            var products = new ArrayList<ProductEntity>();
            products.add(productEntity);
            var page = new PageImpl<>(products.stream().map(ProductMapper.MAPPER::toModel).toList(), pageable, 1);

            when(productService.search(any(), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/v1/products")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(productId.toString()));

            verify(productService, times(1)).search(any(), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("PUT /v1/products/{id} - Atualizar produto")
    class UpdateProduct {

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
        void shouldUpdateProduct() throws Exception {
            productEntity.setName("Pizza Prosciutto");
            productEntity.setPrice(new BigDecimal("30.00"));

            when(productService.update(eq(productId), any(ProductModel.class), any()))
                    .thenReturn(ProductMapper.MAPPER.toModel(productEntity));

            MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[]{9, 9});

            mockMvc.perform(multipart("/v1/products/{id}", productId)
                            .file(image)
                            .param("restaurant_id", restaurantId.toString())
                            .param("name", "Pizza Prosciutto")
                            .param("description", "With ham")
                            .param("price", "30.00")
                            .param("active", "true")
                            .with(request -> { request.setMethod("PUT"); return request; }))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(productId.toString()))
                    .andExpect(jsonPath("$.name").value("Pizza Prosciutto"))
                    .andExpect(jsonPath("$.price").value(30.00));

            verify(productService, times(1)).update(eq(productId), any(ProductModel.class), any());
        }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar produto inexistente")
        void shouldReturn404WhenUpdatingNonExisting() throws Exception {
            when(productService.update(eq(productId), any(ProductModel.class), any()))
                    .thenThrow(new ResourceNotFoundException("Product", productId.toString()));

            MockMultipartFile emptyImage = new MockMultipartFile("image", "", "application/octet-stream", new byte[0]);

            mockMvc.perform(multipart("/v1/products/{id}", productId)
                            .file(emptyImage)
                            .param("restaurant_id", restaurantId.toString())
                            .param("name", "Pizza Margherita")
                            .param("description", "Traditional pizza")
                            .param("price", "25.50")
                            .param("active", "true")
                            .with(request -> { request.setMethod("PUT"); return request; }))
                    .andExpect(status().isNotFound());

            verify(productService, times(1)).update(eq(productId), any(ProductModel.class), any());
        }
    }

    @Nested
    @DisplayName("DELETE /v1/products/{id} - Deletar produto")
    class DeleteProduct {

    @Test
    @DisplayName("Deve deletar produto com sucesso")
        void shouldDeleteProduct() throws Exception {
            doNothing().when(productService).delete(productId);

            mockMvc.perform(delete("/v1/products/{id}", productId))
                    .andExpect(status().isNoContent());

            verify(productService, times(1)).delete(productId);
        }

    @Test
    @DisplayName("Deve retornar 404 ao deletar produto inexistente")
        void shouldReturn404WhenDeletingNonExisting() throws Exception {
            doThrow(new ResourceNotFoundException("Product", productId.toString())).when(productService).delete(productId);

            mockMvc.perform(delete("/v1/products/{id}", productId))
                    .andExpect(status().isNotFound());

            verify(productService, times(1)).delete(productId);
        }
    }

    @Nested
    @DisplayName("GET /v1/products/{id}/image - Buscar imagem do produto")
    class GetProductImage {

    @Test
    @DisplayName("Deve retornar bytes da imagem com content type")
        void shouldReturnImageBytes() throws Exception {
            var model = ProductMapper.MAPPER.toModel(productEntity);
            model = new ProductModel("image.jpg", new byte[]{1,2,3});

            when(productService.getImage(productId)).thenReturn(model);

            mockMvc.perform(get("/v1/products/{id}/image", productId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("image/jpeg"))
                    .andExpect(content().bytes(new byte[]{1,2,3}));

            verify(productService, times(1)).getImage(productId);
        }
    }

    @Nested
    @DisplayName("DELETE /v1/products/{id}/image - Deletar imagem do produto")
    class DeleteProductImage {

    @Test
    @DisplayName("Deve deletar imagem e retornar resposta do produto")
        void shouldDeleteImage() throws Exception {
            when(productService.deleteImage(productId)).thenReturn(ProductMapper.MAPPER.toModel(productEntity));

            mockMvc.perform(delete("/v1/products/{id}/image", productId))
                    .andExpect(status().isOk());

            verify(productService, times(1)).deleteImage(productId);
        }
    }
}

