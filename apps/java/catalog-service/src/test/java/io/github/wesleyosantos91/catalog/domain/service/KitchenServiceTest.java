package io.github.wesleyosantos91.catalog.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.wesleyosantos91.catalog.infrastructure.database.entity.KitchenEntity;
import io.github.wesleyosantos91.catalog.domain.exception.BusinessException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceAlreadyExistsException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
import io.github.wesleyosantos91.catalog.domain.model.KitchenModel;
import io.github.wesleyosantos91.catalog.infrastructure.database.repository.KitchenRepository;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("KitchenService - Unit Tests")
class KitchenServiceTest {

    @Mock
    private KitchenRepository kitchenRepository;

    @InjectMocks
    private KitchenService kitchenService;

    private KitchenEntity kitchenEntity;
    private KitchenModel kitchenModel;
    private UUID kitchenId;

    @BeforeEach
    void setUp() {
        kitchenId = UUID.randomUUID();
        kitchenEntity = new KitchenEntity();
        kitchenEntity.setId(kitchenId);
        kitchenEntity.setName("Italiana");

        kitchenModel = new KitchenModel("Italiana");
    }

    @Nested
    @DisplayName("create() - Criar cozinha")
    class CreateKitchen {

        @Test
        @DisplayName("Deve criar uma cozinha com sucesso")
        void deveCriarCozinhaComSucesso() {
            when(kitchenRepository.existsByName("Italiana")).thenReturn(false);
            when(kitchenRepository.save(any(KitchenEntity.class))).thenReturn(kitchenEntity);

            KitchenModel result = kitchenService.create(kitchenModel);

            assertNotNull(result);
            assertEquals("Italiana", result.name());
            verify(kitchenRepository, times(1)).existsByName("Italiana");
            verify(kitchenRepository, times(1)).save(any(KitchenEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando nome já existe")
        void deveLancarExcecaoQuandoNomeJaExiste() {
            when(kitchenRepository.existsByName("Italiana")).thenReturn(true);

            ResourceAlreadyExistsException exception = assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> kitchenService.create(kitchenModel)
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
                    () -> kitchenService.create(kitchenModel)
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
                    () -> kitchenService.create(kitchenModel)
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

            KitchenModel result = kitchenService.findById(kitchenId);

            assertNotNull(result);
            assertEquals(kitchenId, result.id());
            assertEquals("Italiana", result.name());
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
            KitchenModel queryModel = new KitchenModel("Italiana");
            var kitchens = new ArrayList<KitchenEntity>();
            kitchens.add(kitchenEntity);
            Page<KitchenEntity> page = new PageImpl<>(kitchens, pageable, 1);

            when(kitchenRepository.findByFilters("Italiana", pageable)).thenReturn(page);

            Page<KitchenModel> result = kitchenService.search(queryModel, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("Italiana", result.getContent().get(0).name());
            verify(kitchenRepository, times(1)).findByFilters("Italiana", pageable);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando nenhuma cozinha encontrada")
        void deveRetornarPaginaVaziaQuandoNenhumaCozinhaEncontrada() {
            Pageable pageable = PageRequest.of(0, 10);
            KitchenModel queryModel = new KitchenModel("Inexistente");
            Page<KitchenEntity> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

            when(kitchenRepository.findByFilters("Inexistente", pageable)).thenReturn(emptyPage);

            Page<KitchenModel> result = kitchenService.search(queryModel, pageable);

            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            verify(kitchenRepository, times(1)).findByFilters("Inexistente", pageable);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando há erro de banco de dados")
        void deveLancarBusinessExceptionQuandoErroBancoDados() {
            Pageable pageable = PageRequest.of(0, 10);
            KitchenModel queryModel = new KitchenModel("Italiana");

            when(kitchenRepository.findByFilters(anyString(), any(Pageable.class)))
                    .thenThrow(new DataAccessException("Database error") {});

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> kitchenService.search(queryModel, pageable)
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
            KitchenModel updateModel = new KitchenModel("Italiana Moderna");
            KitchenEntity updatedEntity = new KitchenEntity();
            updatedEntity.setId(kitchenId);
            updatedEntity.setName("Italiana Moderna");

            when(kitchenRepository.findById(kitchenId)).thenReturn(Optional.of(kitchenEntity));
            when(kitchenRepository.existsByName("Italiana Moderna")).thenReturn(false);
            when(kitchenRepository.save(any(KitchenEntity.class))).thenReturn(updatedEntity);

            KitchenModel result = kitchenService.update(kitchenId, updateModel);

            assertNotNull(result);
            assertEquals("Italiana Moderna", result.name());
            verify(kitchenRepository, times(1)).findById(kitchenId);
            verify(kitchenRepository, times(1)).save(any(KitchenEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando cozinha não existe")
        void deveLancarExcecaoQuandoCozinhaNaoExiste() {
            when(kitchenRepository.findById(kitchenId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> kitchenService.update(kitchenId, kitchenModel)
            );

            assertNotNull(exception);
            verify(kitchenRepository, times(1)).findById(kitchenId);
            verify(kitchenRepository, never()).save(any(KitchenEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando novo nome já existe")
        void deveLancarExcecaoQuandoNovoNomeJaExiste() {
            KitchenModel updateRequest = new KitchenModel("Japonesa");

            when(kitchenRepository.findById(kitchenId)).thenReturn(Optional.of(kitchenEntity));
            when(kitchenRepository.existsByName("Japonesa")).thenReturn(true);

            ResourceAlreadyExistsException exception = assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> kitchenService.update(kitchenId, updateRequest)
            );

            assertNotNull(exception);
            verify(kitchenRepository, never()).save(any(KitchenEntity.class));
        }

        @Test
        @DisplayName("Deve atualizar cozinha com mesmo nome sem verificar duplicata")
        void deveAtualizarCozinhaComMesmoNomeSemVerificarDuplicata() {
            KitchenModel updateRequest = new KitchenModel("Italiana");

            when(kitchenRepository.findById(kitchenId)).thenReturn(Optional.of(kitchenEntity));
            when(kitchenRepository.save(any(KitchenEntity.class))).thenReturn(kitchenEntity);

            KitchenModel result = kitchenService.update(kitchenId, updateRequest);

            assertNotNull(result);
            assertEquals("Italiana", result.name());
            verify(kitchenRepository, times(1)).findById(kitchenId);
            verify(kitchenRepository, never()).existsByName(anyString());
            verify(kitchenRepository, times(1)).save(any(KitchenEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando há violação de integridade")
        void deveLancarExcecaoQuandoViolacaoIntegridade() {
            KitchenModel updateRequest = new KitchenModel("Italiana Moderna");

            when(kitchenRepository.findById(kitchenId)).thenReturn(Optional.of(kitchenEntity));
            when(kitchenRepository.existsByName("Italiana Moderna")).thenReturn(false);
            when(kitchenRepository.save(any(KitchenEntity.class)))
                    .thenThrow(new DataIntegrityViolationException("Duplicate key"));

            ResourceAlreadyExistsException exception = assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> kitchenService.update(kitchenId, updateRequest)
            );

            assertNotNull(exception);
            verify(kitchenRepository, times(1)).save(any(KitchenEntity.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando há erro de banco de dados")
        void deveLancarBusinessExceptionQuandoErroBancoDados() {
            KitchenModel updateRequest = new KitchenModel("Italiana Moderna");

            when(kitchenRepository.findById(kitchenId)).thenReturn(Optional.of(kitchenEntity));
            when(kitchenRepository.existsByName("Italiana Moderna")).thenReturn(false);
            when(kitchenRepository.save(any(KitchenEntity.class)))
                    .thenThrow(new DataAccessException("Database error") {});

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> kitchenService.update(kitchenId, updateRequest)
            );

            assertNotNull(exception);
            assertEquals("DATABASE_ERROR", exception.getErrorCode());
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

        @Test
        @DisplayName("Deve lançar BusinessException quando há violação de integridade referencial")
        void deveLancarBusinessExceptionQuandoViolacaoIntegridadeReferencial() {
            when(kitchenRepository.existsById(kitchenId)).thenReturn(true);
            doThrow(new DataIntegrityViolationException("Referenced by other entities"))
                    .when(kitchenRepository).deleteById(kitchenId);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> kitchenService.delete(kitchenId)
            );

            assertNotNull(exception);
            assertEquals("DATA_INTEGRITY_VIOLATION", exception.getErrorCode());
            verify(kitchenRepository, times(1)).existsById(kitchenId);
            verify(kitchenRepository, times(1)).deleteById(kitchenId);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando há erro de banco de dados")
        void deveLancarBusinessExceptionQuandoErroBancoDados() {
            when(kitchenRepository.existsById(kitchenId)).thenReturn(true);
            doThrow(new DataAccessException("Database error") {})
                    .when(kitchenRepository).deleteById(kitchenId);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> kitchenService.delete(kitchenId)
            );

            assertNotNull(exception);
            assertEquals("DATABASE_ERROR", exception.getErrorCode());
            verify(kitchenRepository, times(1)).existsById(kitchenId);
            verify(kitchenRepository, times(1)).deleteById(kitchenId);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando EmptyResultDataAccessException é lançada")
        void deveLancarResourceNotFoundExceptionQuandoEmptyResultDataAccessException() {
            UUID id = UUID.randomUUID();

            when(kitchenRepository.existsById(id)).thenReturn(true);
            doThrow(new EmptyResultDataAccessException(1)).when(kitchenRepository).deleteById(id);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> kitchenService.delete(id)
            );

            assertEquals("Resource Kitchen with identifier " + id + " not found", exception.getMessage());
        }
    }
}


