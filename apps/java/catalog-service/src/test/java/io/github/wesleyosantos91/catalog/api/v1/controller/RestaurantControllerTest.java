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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wesleyosantos91.catalog.api.v1.request.RestaurantRequest;
import io.github.wesleyosantos91.catalog.domain.entity.KitchenEntity;
import io.github.wesleyosantos91.catalog.domain.entity.RestaurantEntity;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceAlreadyExistsException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
import io.github.wesleyosantos91.catalog.domain.model.RestaurantModel;
import io.github.wesleyosantos91.catalog.core.mapper.RestaurantMapper;
import io.github.wesleyosantos91.catalog.domain.service.RestaurantService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testes unitários para RestaurantController.
 * Usa @WebMvcTest para testar apenas a camada web, mockando o service.
 */
@WebMvcTest(RestaurantController.class)
@DisplayName("RestaurantController - Unit Tests")
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestaurantService restaurantService;

    private RestaurantEntity restaurantEntity;
    private KitchenEntity kitchenEntity;
    private RestaurantRequest restaurantRequest;
    private UUID restaurantId;
    private UUID kitchenId;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        kitchenId = UUID.randomUUID();

        kitchenEntity = new KitchenEntity();
        kitchenEntity.setId(kitchenId);
        kitchenEntity.setName("Italiana");

        restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(restaurantId);
        restaurantEntity.setName("Restaurante Italiano");
        restaurantEntity.setKitchen(kitchenEntity);
        restaurantEntity.setActive(true);
        restaurantEntity.setDeliveryFee(new BigDecimal("10.00"));

        restaurantRequest = new RestaurantRequest(
                "Restaurante Italiano",
                kitchenId,
                true,
                new BigDecimal("10.00")
        );
    }

    @Nested
    @DisplayName("POST /v1/restaurants - Criar restaurante")
    class CreateRestaurant {

        @Test
        @DisplayName("Deve criar um restaurante com sucesso e retornar 201")
        void deveCriarRestauranteComSucesso() throws Exception {
            when(restaurantService.create(any(RestaurantModel.class))).thenReturn(RestaurantMapper.MAPPER.toModel(restaurantEntity));

            mockMvc.perform(post("/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(restaurantRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(restaurantId.toString()))
                    .andExpect(jsonPath("$.name").value("Restaurante Italiano"))
                    .andExpect(jsonPath("$.kitchen.id").value(kitchenId.toString()))
                    .andExpect(jsonPath("$.kitchen.name").value("Italiana"))
                    .andExpect(jsonPath("$.active").value(true))
                    .andExpect(jsonPath("$.delivery_fee").value(10.00));

            verify(restaurantService, times(1)).create(any(RestaurantModel.class));
        }

        @Test
        @DisplayName("Deve retornar 409 quando tentar criar restaurante com nome duplicado")
        void deveRetornar409QuandoNomeDuplicado() throws Exception {
            when(restaurantService.create(any(RestaurantModel.class)))
                    .thenThrow(new ResourceAlreadyExistsException("Restaurant", "name", "Restaurante Italiano"));

            mockMvc.perform(post("/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(restaurantRequest)))
                    .andExpect(status().isConflict());

            verify(restaurantService, times(1)).create(any(RestaurantModel.class));
        }

        @Test
        @DisplayName("Deve retornar 404 quando cozinha não existir")
        void deveRetornar404QuandoCozinhaNaoExistir() throws Exception {
            when(restaurantService.create(any(RestaurantModel.class)))
                    .thenThrow(new ResourceNotFoundException("Kitchen", kitchenId.toString()));

            mockMvc.perform(post("/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(restaurantRequest)))
                    .andExpect(status().isNotFound());

            verify(restaurantService, times(1)).create(any(RestaurantModel.class));
        }
    }

    @Nested
    @DisplayName("GET /v1/restaurants/{id} - Buscar restaurante por ID")
    class GetRestaurantById {

        @Test
        @DisplayName("Deve retornar um restaurante pelo ID com sucesso")
        void deveRetornarRestaurantePorId() throws Exception {
            when(restaurantService.findById(restaurantId)).thenReturn(RestaurantMapper.MAPPER.toModel(restaurantEntity));

            mockMvc.perform(get("/v1/restaurants/{id}", restaurantId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(restaurantId.toString()))
                    .andExpect(jsonPath("$.name").value("Restaurante Italiano"))
                    .andExpect(jsonPath("$.kitchen.id").value(kitchenId.toString()))
                    .andExpect(jsonPath("$.kitchen.name").value("Italiana"))
                    .andExpect(jsonPath("$.active").value(true))
                    .andExpect(jsonPath("$.delivery_fee").value(10.00));

            verify(restaurantService, times(1)).findById(restaurantId);
        }

        @Test
        @DisplayName("Deve retornar 404 quando restaurante não for encontrado")
        void deveRetornar404QuandoRestauranteNaoEncontrado() throws Exception {
            when(restaurantService.findById(restaurantId))
                    .thenThrow(new ResourceNotFoundException("Restaurant", restaurantId.toString()));

            mockMvc.perform(get("/v1/restaurants/{id}", restaurantId))
                    .andExpect(status().isNotFound());

            verify(restaurantService, times(1)).findById(restaurantId);
        }
    }

    @Nested
    @DisplayName("GET /v1/restaurants - Listar restaurantes")
    class SearchRestaurants {

        @Test
        @DisplayName("Deve retornar lista paginada de restaurantes")
        void deveRetornarListaPaginada() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            var restaurants = new ArrayList<RestaurantEntity>();
            restaurants.add(restaurantEntity);
            var page = new PageImpl<>(
                restaurants.stream().map(RestaurantMapper.MAPPER::toModel).toList(),
                pageable,
                1
            );

            when(restaurantService.search(any(RestaurantModel.class), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/v1/restaurants")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(restaurantId.toString()))
                    .andExpect(jsonPath("$.content[0].name").value("Restaurante Italiano"));

            verify(restaurantService, times(1)).search(any(RestaurantModel.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Deve filtrar restaurantes por nome")
        void deveFiltrarRestaurantesPorNome() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            var restaurants = new ArrayList<RestaurantEntity>();
            restaurants.add(restaurantEntity);
            var page = new PageImpl<>(restaurants.stream().map(RestaurantMapper.MAPPER::toModel).toList(), pageable, restaurants.size());

            when(restaurantService.search(any(RestaurantModel.class), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/v1/restaurants")
                            .param("name", "Restaurante Italiano")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());

            verify(restaurantService, times(1)).search(any(RestaurantModel.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Deve filtrar restaurantes por cozinha")
        void deveFiltrarRestaurantesPorCozinha() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            var restaurants = new ArrayList<RestaurantEntity>();
            restaurants.add(restaurantEntity);
            var page = new PageImpl<>(restaurants.stream().map(RestaurantMapper.MAPPER::toModel).toList(), pageable, restaurants.size());

            when(restaurantService.search(any(RestaurantModel.class), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/v1/restaurants")
                            .param("kitchenId", kitchenId.toString())
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());

            verify(restaurantService, times(1)).search(any(RestaurantModel.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Deve filtrar restaurantes por status ativo")
        void deveFiltrarRestaurantesPorStatusAtivo() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            var restaurants = new ArrayList<RestaurantEntity>();
            restaurants.add(restaurantEntity);
            var page = new PageImpl<>(restaurants.stream().map(RestaurantMapper.MAPPER::toModel).toList(), pageable, restaurants.size());

            when(restaurantService.search(any(RestaurantModel.class), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/v1/restaurants")
                            .param("active", "true")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());

            verify(restaurantService, times(1)).search(any(RestaurantModel.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Deve filtrar restaurantes por faixa de taxa de entrega")
        void deveFiltrarRestaurantesPorFaixaTaxaEntrega() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            var restaurants = new ArrayList<RestaurantEntity>();
            restaurants.add(restaurantEntity);
            var page = new PageImpl<>(restaurants.stream().map(RestaurantMapper.MAPPER::toModel).toList(), pageable, restaurants.size());

            when(restaurantService.search(any(RestaurantModel.class), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/v1/restaurants")
                            .param("minDeliveryFee", "5.00")
                            .param("maxDeliveryFee", "15.00")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());

            verify(restaurantService, times(1)).search(any(RestaurantModel.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("PUT /v1/restaurants/{id} - Atualizar restaurante")
    class UpdateRestaurant {

        @Test
        @DisplayName("Deve atualizar um restaurante com sucesso")
        void deveAtualizarRestauranteComSucesso() throws Exception {
            RestaurantRequest updateRequest = new RestaurantRequest(
                    "Restaurante Italiano Moderno",
                    kitchenId,
                    true,
                    new BigDecimal("12.00")
            );
            restaurantEntity.setName("Restaurante Italiano Moderno");
            restaurantEntity.setDeliveryFee(new BigDecimal("12.00"));

            when(restaurantService.update(eq(restaurantId), any(RestaurantModel.class)))
                    .thenReturn(RestaurantMapper.MAPPER.toModel(restaurantEntity));

            mockMvc.perform(put("/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(restaurantId.toString()))
                    .andExpect(jsonPath("$.name").value("Restaurante Italiano Moderno"))
                    .andExpect(jsonPath("$.delivery_fee").value(12.00));

            verify(restaurantService, times(1)).update(eq(restaurantId), any(RestaurantModel.class));
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar restaurante inexistente")
        void deveRetornar404AoAtualizarRestauranteInexistente() throws Exception {
            when(restaurantService.update(eq(restaurantId), any(RestaurantModel.class)))
                    .thenThrow(new ResourceNotFoundException("Restaurant", restaurantId.toString()));

            mockMvc.perform(put("/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(restaurantRequest)))
                    .andExpect(status().isNotFound());

            verify(restaurantService, times(1)).update(eq(restaurantId), any(RestaurantModel.class));
        }

        @Test
        @DisplayName("Deve retornar 404 quando nova cozinha não existir")
        void deveRetornar404QuandoNovaCozinhaNaoExistir() throws Exception {
            UUID newKitchenId = UUID.randomUUID();
            RestaurantRequest updateRequest = new RestaurantRequest(
                    "Restaurante Italiano",
                    newKitchenId,
                    true,
                    new BigDecimal("10.00")
            );

            when(restaurantService.update(eq(restaurantId), any(RestaurantModel.class)))
                    .thenThrow(new ResourceNotFoundException("Kitchen", newKitchenId.toString()));

            mockMvc.perform(put("/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());

            verify(restaurantService, times(1)).update(eq(restaurantId), any(RestaurantModel.class));
        }

        @Test
        @DisplayName("Deve retornar 409 quando novo nome já existir")
        void deveRetornar409QuandoNovoNomeJaExistir() throws Exception {
            RestaurantRequest updateRequest = new RestaurantRequest(
                    "Outro Restaurante",
                    kitchenId,
                    true,
                    new BigDecimal("10.00")
            );

            when(restaurantService.update(eq(restaurantId), any(RestaurantModel.class)))
                    .thenThrow(new ResourceAlreadyExistsException("Restaurant", "name", "Outro Restaurante"));

            mockMvc.perform(put("/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isConflict());

            verify(restaurantService, times(1)).update(eq(restaurantId), any(RestaurantModel.class));
        }
    }

    @Nested
    @DisplayName("DELETE /v1/restaurants/{id} - Deletar restaurante")
    class DeleteRestaurant {

        @Test
        @DisplayName("Deve deletar um restaurante com sucesso")
        void deveDeletarRestauranteComSucesso() throws Exception {
            doNothing().when(restaurantService).delete(restaurantId);

            mockMvc.perform(delete("/v1/restaurants/{id}", restaurantId))
                    .andExpect(status().isNoContent());

            verify(restaurantService, times(1)).delete(restaurantId);
        }

        @Test
        @DisplayName("Deve retornar 404 ao deletar restaurante inexistente")
        void deveRetornar404AoDeletarRestauranteInexistente() throws Exception {
            doThrow(new ResourceNotFoundException("Restaurant", restaurantId.toString()))
                    .when(restaurantService).delete(restaurantId);

            mockMvc.perform(delete("/v1/restaurants/{id}", restaurantId))
                    .andExpect(status().isNotFound());

            verify(restaurantService, times(1)).delete(restaurantId);
        }
    }
}


