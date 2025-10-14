package io.github.wesleyosantos91.catalog.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.wesleyosantos91.catalog.api.v1.request.KitchenQueryRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.KitchenRequest;
import io.github.wesleyosantos91.catalog.domain.entity.KitchenEntity;
import io.github.wesleyosantos91.catalog.domain.exception.BusinessException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceAlreadyExistsException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
import io.github.wesleyosantos91.catalog.domain.repository.KitchenRepository;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Testes unitários para KitchenService.
 * Usa Mockito para mockar o repository e testar a lógica de negócio isoladamente.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("KitchenService - Unit Tests")
class KitchenServiceTest {

    @Mock
    private KitchenRepository kitchenRepository;

    @InjectMocks
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
    @DisplayName("create() - Criar cozinha")
    class CreateKitchen {

        @Test
        @DisplayName("Deve criar uma cozinha com sucesso")
        void deveCriarCozinhaComSucesso() {
            when(kitchenRepository.existsByName("Italiana")).thenReturn(false);
            when(kitchenRepository.save(any(KitchenEntity.class))).thenReturn(kitchenEntity);

            KitchenEntity result = kitchenService.create(kitchenRequest);

            assertNotNull(result);
            assertEquals("Italiana", result.getName());
            verify(kitchenRepository, times(1)).existsByName("Italiana");
            verify(kitchenRepository, times(1)).save(any(KitchenEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando nome já existe")
        void deveLancarExcecaoQuandoNomeJaExiste() {
            when(kitchenRepository.existsByName("Italiana")).thenReturn(true);

            ResourceAlreadyExistsException exception = assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> kitchenService.create(kitchenRequest)
            );

            assertNotNull(exception);
            verify(kitchenRepository, times(1)).existsByName("Italiana");
            verify(kitchenRepository, never()).save(any(KitchenEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando há violação de integridade")
        void deveLancarExcecaoQuandoViolacaoIntegridade() {
            when(kitchenRepository.existsByName("Italiana")).thenReturn(false);
            when(kitchenRepository.save(any(KitchenEntity.class)))
                    .thenThrow(new DataIntegrityViolationException("Duplicate key"));

            ResourceAlreadyExistsException exception = assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> kitchenService.create(kitchenRequest)
            );

            assertNotNull(exception);
            verify(kitchenRepository, times(1)).save(any(KitchenEntity.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando há erro de banco de dados")
        void deveLancarBusinessExceptionQuandoErroBancoDados() {
            when(kitchenRepository.existsByName("Italiana")).thenReturn(false);
            when(kitchenRepository.save(any(KitchenEntity.class)))
                    .thenThrow(new DataAccessException("Database error") {});

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> kitchenService.create(kitchenRequest)
            );

            assertNotNull(exception);
            assertEquals("DATABASE_ERROR", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("findById() - Buscar cozinha por ID")
    class FindKitchenById {

        @Test
        @DisplayName("Deve retornar uma cozinha quando ID existe")
        void deveRetornarCozinhaQuandoIdExiste() {
            when(kitchenRepository.findById(kitchenId)).thenReturn(Optional.of(kitchenEntity));

            KitchenEntity result = kitchenService.findById(kitchenId);

            assertNotNull(result);
            assertEquals(kitchenId, result.getId());
            assertEquals("Italiana", result.getName());
            verify(kitchenRepository, times(1)).findById(kitchenId);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando ID não existe")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            when(kitchenRepository.findById(kitchenId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> kitchenService.findById(kitchenId)
            );

            assertNotNull(exception);
            verify(kitchenRepository, times(1)).findById(kitchenId);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando há erro de banco de dados")
        void deveLancarBusinessExceptionQuandoErroBancoDados() {
            when(kitchenRepository.findById(kitchenId))
                    .thenThrow(new DataAccessException("Database error") {});

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> kitchenService.findById(kitchenId)
            );

            assertNotNull(exception);
            assertEquals("DATABASE_ERROR", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("search() - Buscar cozinhas com filtros")
    class SearchKitchens {

        @Test
        @DisplayName("Deve retornar página de cozinhas")
        void deveRetornarPaginaDeCozinhas() {
            Pageable pageable = PageRequest.of(0, 10);
            KitchenQueryRequest queryRequest = new KitchenQueryRequest("Italiana");
            var kitchens = new ArrayList<KitchenEntity>();
            kitchens.add(kitchenEntity);
            Page<KitchenEntity> page = new PageImpl<>(kitchens, pageable, 1);

            when(kitchenRepository.findByFilters("Italiana", pageable)).thenReturn(page);

            Page<KitchenEntity> result = kitchenService.search(queryRequest, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("Italiana", result.getContent().get(0).getName());
            verify(kitchenRepository, times(1)).findByFilters("Italiana", pageable);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando nenhuma cozinha encontrada")
        void deveRetornarPaginaVaziaQuandoNenhumaCozinhaEncontrada() {
            Pageable pageable = PageRequest.of(0, 10);
            KitchenQueryRequest queryRequest = new KitchenQueryRequest("Inexistente");
            Page<KitchenEntity> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

            when(kitchenRepository.findByFilters("Inexistente", pageable)).thenReturn(emptyPage);

            Page<KitchenEntity> result = kitchenService.search(queryRequest, pageable);

            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            verify(kitchenRepository, times(1)).findByFilters("Inexistente", pageable);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando há erro de banco de dados")
        void deveLancarBusinessExceptionQuandoErroBancoDados() {
            Pageable pageable = PageRequest.of(0, 10);
            KitchenQueryRequest queryRequest = new KitchenQueryRequest("Italiana");

            when(kitchenRepository.findByFilters(anyString(), any(Pageable.class)))
                    .thenThrow(new DataAccessException("Database error") {});

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> kitchenService.search(queryRequest, pageable)
            );

            assertNotNull(exception);
            assertEquals("DATABASE_ERROR", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("update() - Atualizar cozinha")
    class UpdateKitchen {

        @Test
        @DisplayName("Deve atualizar uma cozinha com sucesso")
        void deveAtualizarCozinhaComSucesso() {
            KitchenRequest updateRequest = new KitchenRequest("Italiana Moderna");
            KitchenEntity updatedEntity = new KitchenEntity();
            updatedEntity.setId(kitchenId);
            updatedEntity.setName("Italiana Moderna");

            when(kitchenRepository.findById(kitchenId)).thenReturn(Optional.of(kitchenEntity));
            when(kitchenRepository.existsByName("Italiana Moderna")).thenReturn(false);
            when(kitchenRepository.save(any(KitchenEntity.class))).thenReturn(updatedEntity);

            KitchenEntity result = kitchenService.update(kitchenId, updateRequest);

            assertNotNull(result);
            assertEquals("Italiana Moderna", result.getName());
            verify(kitchenRepository, times(1)).findById(kitchenId);
            verify(kitchenRepository, times(1)).save(any(KitchenEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando cozinha não existe")
        void deveLancarExcecaoQuandoCozinhaNaoExiste() {
            when(kitchenRepository.findById(kitchenId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> kitchenService.update(kitchenId, kitchenRequest)
            );

            assertNotNull(exception);
            verify(kitchenRepository, times(1)).findById(kitchenId);
            verify(kitchenRepository, never()).save(any(KitchenEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando novo nome já existe")
        void deveLancarExcecaoQuandoNovoNomeJaExiste() {
            KitchenRequest updateRequest = new KitchenRequest("Japonesa");

            when(kitchenRepository.findById(kitchenId)).thenReturn(Optional.of(kitchenEntity));
            when(kitchenRepository.existsByName("Japonesa")).thenReturn(true);

            ResourceAlreadyExistsException exception = assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> kitchenService.update(kitchenId, updateRequest)
            );

            assertNotNull(exception);
            verify(kitchenRepository, never()).save(any(KitchenEntity.class));
        }
    }

    @Nested
    @DisplayName("delete() - Deletar cozinha")
    class DeleteKitchen {

        @Test
        @DisplayName("Deve deletar uma cozinha com sucesso")
        void deveDeletarCozinhaComSucesso() {
            when(kitchenRepository.existsById(kitchenId)).thenReturn(true);

            kitchenService.delete(kitchenId);

            verify(kitchenRepository, times(1)).existsById(kitchenId);
            verify(kitchenRepository, times(1)).deleteById(kitchenId);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando cozinha não existe")
        void deveLancarExcecaoQuandoCozinhaNaoExiste() {
            when(kitchenRepository.existsById(kitchenId)).thenReturn(false);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> kitchenService.delete(kitchenId)
            );

            assertNotNull(exception);
            verify(kitchenRepository, times(1)).existsById(kitchenId);
            verify(kitchenRepository, never()).deleteById(any(UUID.class));
        }
    }
}
