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
import io.github.wesleyosantos91.catalog.api.v1.request.KitchenRequest;
import io.github.wesleyosantos91.catalog.domain.entity.KitchenEntity;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceAlreadyExistsException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
import io.github.wesleyosantos91.catalog.domain.model.KitchenModel;
import io.github.wesleyosantos91.catalog.core.mapper.KitchenMapper;
import io.github.wesleyosantos91.catalog.domain.service.KitchenService;
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
 * Testes unitários para KitchenController.
 * Usa @WebMvcTest para testar apenas a camada web, mockando o service.
 */
@WebMvcTest(KitchenController.class)
@DisplayName("KitchenController - Unit Tests")
class KitchenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KitchenService kitchenService;

    private KitchenEntity kitchenEntity;
    private KitchenRequest kitchenRequest;
    private UUID kitchenId;

    @BeforeEach
    void setUp() {
        kitchenId = UUID.randomUUID();
        kitchenEntity = new KitchenEntity();
        kitchenEntity.setId(kitchenId);
        kitchenEntity.setName("Italiana");

        kitchenRequest = new KitchenRequest("Italiana");
    }

    @Nested
    @DisplayName("POST /v1/kitchens - Criar cozinha")
    class CreateKitchen {

        @Test
        @DisplayName("Deve criar uma cozinha com sucesso e retornar 201")
        void devecriarCozinhaComSucesso() throws Exception {
            when(kitchenService.create(any(KitchenModel.class))).thenReturn(KitchenMapper.MAPPER.toModel(kitchenEntity));

            mockMvc.perform(post("/v1/kitchens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(kitchenRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(kitchenId.toString()))
                    .andExpect(jsonPath("$.name").value("Italiana"));

            verify(kitchenService, times(1)).create(any(KitchenModel.class));
        }

        @Test
        @DisplayName("Deve retornar 409 quando tentar criar cozinha com nome duplicado")
        void deveRetornar409QuandoNomeDuplicado() throws Exception {
            when(kitchenService.create(any(KitchenModel.class)))
                    .thenThrow(new ResourceAlreadyExistsException("Kitchen", "name", "Italiana"));

            mockMvc.perform(post("/v1/kitchens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(kitchenRequest)))
                    .andExpect(status().isConflict());

            verify(kitchenService, times(1)).create(any(KitchenModel.class));
        }
    }

    @Nested
    @DisplayName("GET /v1/kitchens/{id} - Buscar cozinha por ID")
    class GetKitchenById {

        @Test
        @DisplayName("Deve retornar uma cozinha pelo ID com sucesso")
        void deveRetornarCozinhaPorId() throws Exception {
            when(kitchenService.findById(kitchenId)).thenReturn(KitchenMapper.MAPPER.toModel(kitchenEntity));

            mockMvc.perform(get("/v1/kitchens/{id}", kitchenId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(kitchenId.toString()))
                    .andExpect(jsonPath("$.name").value("Italiana"));

            verify(kitchenService, times(1)).findById(kitchenId);
        }

        @Test
        @DisplayName("Deve retornar 404 quando cozinha não for encontrada")
        void deveRetornar404QuandoCozinhaNaoEncontrada() throws Exception {
            when(kitchenService.findById(kitchenId))
                    .thenThrow(new ResourceNotFoundException("Kitchen", kitchenId.toString()));

            mockMvc.perform(get("/v1/kitchens/{id}", kitchenId))
                    .andExpect(status().isNotFound());

            verify(kitchenService, times(1)).findById(kitchenId);
        }
    }

    @Nested
    @DisplayName("GET /v1/kitchens - Listar cozinhas")
    class SearchKitchens {

        @Test
        @DisplayName("Deve retornar lista paginada de cozinhas")
        void deveRetornarListaPaginada() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            var kitchens = new ArrayList<KitchenEntity>();
            kitchens.add(kitchenEntity);
            var page = new PageImpl<>(kitchens.stream().map(KitchenMapper.MAPPER::toModel).toList(), pageable, kitchens.size());

            when(kitchenService.search(any(KitchenModel.class), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/v1/kitchens")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(kitchenId.toString()))
                    .andExpect(jsonPath("$.content[0].name").value("Italiana"));

            verify(kitchenService, times(1)).search(any(KitchenModel.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Deve filtrar cozinhas por nome")
        void deveFiltrarCozinhasPorNome() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            var kitchens = new ArrayList<KitchenEntity>();
            kitchens.add(kitchenEntity);
            var page = new PageImpl<>(kitchens.stream().map(KitchenMapper.MAPPER::toModel).toList(), pageable, kitchens.size());

            when(kitchenService.search(any(KitchenModel.class), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/v1/kitchens")
                            .param("name", "Italiana")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());

            verify(kitchenService, times(1)).search(any(KitchenModel.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("PUT /v1/kitchens/{id} - Atualizar cozinha")
    class UpdateKitchen {

        @Test
        @DisplayName("Deve atualizar uma cozinha com sucesso")
        void deveAtualizarCozinhaComSucesso() throws Exception {
            KitchenRequest updateRequest = new KitchenRequest("Italiana Moderna");
            kitchenEntity.setName("Italiana Moderna");

            when(kitchenService.update(eq(kitchenId), any(KitchenModel.class)))
                    .thenReturn(KitchenMapper.MAPPER.toModel(kitchenEntity));

            mockMvc.perform(put("/v1/kitchens/{id}", kitchenId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(kitchenId.toString()))
                    .andExpect(jsonPath("$.name").value("Italiana Moderna"));

            verify(kitchenService, times(1)).update(eq(kitchenId), any(KitchenModel.class));
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar cozinha inexistente")
        void deveRetornar404AoAtualizarCozinhaInexistente() throws Exception {
            when(kitchenService.update(eq(kitchenId), any(KitchenModel.class)))
                    .thenThrow(new ResourceNotFoundException("Kitchen", kitchenId.toString()));

            mockMvc.perform(put("/v1/kitchens/{id}", kitchenId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(kitchenRequest)))
                    .andExpect(status().isNotFound());

            verify(kitchenService, times(1)).update(eq(kitchenId), any(KitchenModel.class));
        }
    }

    @Nested
    @DisplayName("DELETE /v1/kitchens/{id} - Deletar cozinha")
    class DeleteKitchen {

        @Test
        @DisplayName("Deve deletar uma cozinha com sucesso")
        void deveDeletarCozinhaComSucesso() throws Exception {
            doNothing().when(kitchenService).delete(kitchenId);

            mockMvc.perform(delete("/v1/kitchens/{id}", kitchenId))
                    .andExpect(status().isNoContent());

            verify(kitchenService, times(1)).delete(kitchenId);
        }

        @Test
        @DisplayName("Deve retornar 404 ao deletar cozinha inexistente")
        void deveRetornar404AoDeletarCozinhaInexistente() throws Exception {
            doThrow(new ResourceNotFoundException("Kitchen", kitchenId.toString()))
                    .when(kitchenService).delete(kitchenId);

            mockMvc.perform(delete("/v1/kitchens/{id}", kitchenId))
                    .andExpect(status().isNotFound());

            verify(kitchenService, times(1)).delete(kitchenId);
        }
    }
}



