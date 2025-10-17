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

import io.github.wesleyosantos91.catalog.domain.entity.KitchenEntity;
import io.github.wesleyosantos91.catalog.domain.entity.RestaurantEntity;
import io.github.wesleyosantos91.catalog.domain.exception.BusinessException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceAlreadyExistsException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
import io.github.wesleyosantos91.catalog.domain.model.RestaurantModel;
import io.github.wesleyosantos91.catalog.domain.repository.KitchenRepository;
import io.github.wesleyosantos91.catalog.domain.repository.RestaurantRepository;
import java.math.BigDecimal;
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
@DisplayName("RestaurantService - Unit Tests")
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private KitchenRepository kitchenRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private RestaurantEntity restaurantEntity;
    private KitchenEntity kitchenEntity;
    private RestaurantModel restaurantModel;
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
        restaurantEntity.setName("Restaurante Teste");
        restaurantEntity.setKitchen(kitchenEntity);
        restaurantEntity.setActive(true);
        restaurantEntity.setDeliveryFee(new BigDecimal("10.00"));

        restaurantModel = new RestaurantModel(
                "Restaurante Teste",
                kitchenId,
                true,
                new BigDecimal("10.00")
        );
    }

    @Nested
    @DisplayName("create() - Criar restaurante")
    class CreateRestaurant {

        @Test
        @DisplayName("Deve criar um restaurante com sucesso")
        void deveCriarRestauranteComSucesso() {
            when(kitchenRepository.existsById(kitchenId)).thenReturn(true);
            when(restaurantRepository.existsByName("Restaurante Teste")).thenReturn(false);
            when(restaurantRepository.save(any(RestaurantEntity.class))).thenReturn(restaurantEntity);

            RestaurantModel result = restaurantService.create(restaurantModel);

            assertNotNull(result);
            assertEquals("Restaurante Teste", result.name());
            assertEquals(kitchenId, result.kitchen().id());
            verify(kitchenRepository, times(1)).existsById(kitchenId);
            verify(restaurantRepository, times(1)).existsByName("Restaurante Teste");
            verify(restaurantRepository, times(1)).save(any(RestaurantEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando cozinha não existe")
        void deveLancarExcecaoQuandoCozinhaNaoExiste() {
            when(kitchenRepository.existsById(kitchenId)).thenReturn(false);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> restaurantService.create(restaurantModel)
            );

            assertNotNull(exception);
            verify(kitchenRepository, times(1)).existsById(kitchenId);
            verify(restaurantRepository, never()).existsByName(anyString());
            verify(restaurantRepository, never()).save(any(RestaurantEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando nome já existe")
        void deveLancarExcecaoQuandoNomeJaExiste() {
            when(kitchenRepository.existsById(kitchenId)).thenReturn(true);
            when(restaurantRepository.existsByName("Restaurante Teste")).thenReturn(true);

            ResourceAlreadyExistsException exception = assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> restaurantService.create(restaurantModel)
            );

            assertNotNull(exception);
            verify(kitchenRepository, times(1)).existsById(kitchenId);
            verify(restaurantRepository, times(1)).existsByName("Restaurante Teste");
            verify(restaurantRepository, never()).save(any(RestaurantEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando há violação de integridade")
        void deveLancarExcecaoQuandoViolacaoIntegridade() {
            when(kitchenRepository.existsById(kitchenId)).thenReturn(true);
            when(restaurantRepository.existsByName("Restaurante Teste")).thenReturn(false);
            when(restaurantRepository.save(any(RestaurantEntity.class)))
                    .thenThrow(new DataIntegrityViolationException("Duplicate key"));

            ResourceAlreadyExistsException exception = assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> restaurantService.create(restaurantModel)
            );

            assertNotNull(exception);
            verify(restaurantRepository, times(1)).save(any(RestaurantEntity.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando há erro de banco de dados")
        void deveLancarBusinessExceptionQuandoErroBancoDados() {
            when(kitchenRepository.existsById(kitchenId)).thenReturn(true);
            when(restaurantRepository.existsByName("Restaurante Teste")).thenReturn(false);
            when(restaurantRepository.save(any(RestaurantEntity.class)))
                    .thenThrow(new DataAccessException("Database error") {});

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> restaurantService.create(restaurantModel)
            );

            assertNotNull(exception);
            assertEquals("DATABASE_ERROR", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("findById() - Buscar restaurante por ID")
    class FindRestaurantById {

        @Test
        @DisplayName("Deve retornar um restaurante quando ID existe")
        void deveRetornarRestauranteQuandoIdExiste() {
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurantEntity));

            RestaurantModel result = restaurantService.findById(restaurantId);

            assertNotNull(result);
            assertEquals(restaurantId, result.id());
            assertEquals("Restaurante Teste", result.name());
            verify(restaurantRepository, times(1)).findById(restaurantId);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando ID não existe")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> restaurantService.findById(restaurantId)
            );

            assertNotNull(exception);
            verify(restaurantRepository, times(1)).findById(restaurantId);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando há erro de banco de dados")
        void deveLancarBusinessExceptionQuandoErroBancoDados() {
            when(restaurantRepository.findById(restaurantId))
                    .thenThrow(new DataAccessException("Database error") {});

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> restaurantService.findById(restaurantId)
            );

            assertNotNull(exception);
            assertEquals("DATABASE_ERROR", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("search() - Buscar restaurantes com filtros")
    class SearchRestaurants {

        @Test
        @DisplayName("Deve retornar página de restaurantes")
        void deveRetornarPaginaDeRestaurantes() {
            Pageable pageable = PageRequest.of(0, 10);
            RestaurantModel queryRequest = new RestaurantModel("Restaurante Teste", kitchenId, true, new BigDecimal("5.00"));
            var restaurants = new ArrayList<RestaurantEntity>();
            restaurants.add(restaurantEntity);
            Page<RestaurantEntity> page = new PageImpl<>(restaurants, pageable, 1);

            when(restaurantRepository.findByFilters(
                    "Restaurante Teste",
                    kitchenId,
                    true,
                    null,
                    null,
                    pageable
            )).thenReturn(page);

            Page<RestaurantModel> result = restaurantService.search(queryRequest, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("Restaurante Teste", result.getContent().get(0).name());
            verify(restaurantRepository, times(1)).findByFilters(
                    "Restaurante Teste",
                    kitchenId,
                    true,
                    null,
                    null,
                    pageable
            );
        }

        @Test
        @DisplayName("Deve retornar página vazia quando nenhum restaurante encontrado")
        void deveRetornarPaginaVaziaQuandoNenhumRestauranteEncontrado() {
            Pageable pageable = PageRequest.of(0, 10);
            RestaurantModel queryRequest = new RestaurantModel("Inexistente", null, null, null);
            Page<RestaurantEntity> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

            when(restaurantRepository.findByFilters(
                    "Inexistente",
                    null,
                    null,
                    null,
                    null,
                    pageable
            )).thenReturn(emptyPage);

            Page<RestaurantModel> result = restaurantService.search(queryRequest, pageable);

            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            verify(restaurantRepository, times(1)).findByFilters(
                    "Inexistente",
                    null,
                    null,
                    null,
                    null,
                    pageable
            );
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando há erro de banco de dados")
        void deveLancarBusinessExceptionQuandoErroBancoDados() {
            Pageable pageable = PageRequest.of(0, 10);
            RestaurantModel queryRequest = new RestaurantModel("Restaurante Teste", null, null, null);

            when(restaurantRepository.findByFilters(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(Pageable.class)
            )).thenThrow(new DataAccessException("Database error") {});

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> restaurantService.search(queryRequest, pageable)
            );

            assertNotNull(exception);
            assertEquals("DATABASE_ERROR", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("update() - Atualizar restaurante")
    class UpdateRestaurant {

        @Test
        @DisplayName("Deve atualizar um restaurante com sucesso")
        void deveAtualizarRestauranteComSucesso() {
            RestaurantModel updateRequest = new RestaurantModel(
                    "Restaurante Atualizado",
                    kitchenId,
                    true,
                    new BigDecimal("12.00")
            );
            RestaurantEntity updatedEntity = new RestaurantEntity();
            updatedEntity.setId(restaurantId);
            updatedEntity.setName("Restaurante Atualizado");
            updatedEntity.setKitchen(kitchenEntity);
            updatedEntity.setActive(true);
            updatedEntity.setDeliveryFee(new BigDecimal("12.00"));

            when(restaurantRepository.findByIdWithKitchen(restaurantId)).thenReturn(Optional.of(restaurantEntity));
            when(restaurantRepository.existsByName("Restaurante Atualizado")).thenReturn(false);
            when(restaurantRepository.save(any(RestaurantEntity.class))).thenReturn(updatedEntity);

            RestaurantModel result = restaurantService.update(restaurantId, updateRequest);

            assertNotNull(result);
            assertEquals("Restaurante Atualizado", result.name());
            verify(restaurantRepository, times(1)).findByIdWithKitchen(restaurantId);
            verify(restaurantRepository, times(1)).save(any(RestaurantEntity.class));
        }

        @Test
        @DisplayName("Deve atualizar restaurante mantendo mesmo nome")
        void deveAtualizarRestauranteManvendoMesmoNome() {
            RestaurantModel updateRequest = new RestaurantModel(
                    "Restaurante Teste",
                    kitchenId,
                    false,
                    new BigDecimal("15.00")
            );
            RestaurantEntity updatedEntity = new RestaurantEntity();
            updatedEntity.setId(restaurantId);
            updatedEntity.setName("Restaurante Teste");
            updatedEntity.setKitchen(kitchenEntity);
            updatedEntity.setActive(false);
            updatedEntity.setDeliveryFee(new BigDecimal("15.00"));

            when(restaurantRepository.findByIdWithKitchen(restaurantId)).thenReturn(Optional.of(restaurantEntity));
            when(restaurantRepository.save(any(RestaurantEntity.class))).thenReturn(updatedEntity);

            RestaurantModel result = restaurantService.update(restaurantId, updateRequest);

            assertNotNull(result);
            assertEquals("Restaurante Teste", result.name());
            verify(restaurantRepository, times(1)).findByIdWithKitchen(restaurantId);
            verify(restaurantRepository, never()).existsByName(anyString());
            verify(restaurantRepository, times(1)).save(any(RestaurantEntity.class));
        }

        @Test
        @DisplayName("Deve atualizar restaurante com nova cozinha")
        void deveAtualizarRestauranteComNovaCozinha() {
            UUID newKitchenId = UUID.randomUUID();
            RestaurantModel updateRequest = new RestaurantModel(
                    "Restaurante Teste",
                    newKitchenId,
                    true,
                    new BigDecimal("10.00")
            );

            when(restaurantRepository.findByIdWithKitchen(restaurantId)).thenReturn(Optional.of(restaurantEntity));
            when(kitchenRepository.existsById(newKitchenId)).thenReturn(true);
            when(restaurantRepository.save(any(RestaurantEntity.class))).thenReturn(restaurantEntity);

            RestaurantModel result = restaurantService.update(restaurantId, updateRequest);

            assertNotNull(result);
            verify(kitchenRepository, times(1)).existsById(newKitchenId);
            verify(restaurantRepository, times(1)).findByIdWithKitchen(restaurantId);
            verify(restaurantRepository, times(1)).save(any(RestaurantEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando restaurante não existe")
        void deveLancarExcecaoQuandoRestauranteNaoExiste() {
            when(restaurantRepository.findByIdWithKitchen(restaurantId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> restaurantService.update(restaurantId, restaurantModel)
            );

            assertNotNull(exception);
            verify(restaurantRepository, times(1)).findByIdWithKitchen(restaurantId);
            verify(restaurantRepository, never()).save(any(RestaurantEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando nova cozinha não existe")
        void deveLancarExcecaoQuandoNovaCozinhaNaoExiste() {
            UUID newKitchenId = UUID.randomUUID();
            RestaurantModel updateRequest = new RestaurantModel(
                    "Restaurante Teste",
                    newKitchenId,
                    true,
                    new BigDecimal("10.00")
            );

            when(restaurantRepository.findByIdWithKitchen(restaurantId)).thenReturn(Optional.of(restaurantEntity));
            when(kitchenRepository.existsById(newKitchenId)).thenReturn(false);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> restaurantService.update(restaurantId, updateRequest)
            );

            assertNotNull(exception);
            verify(kitchenRepository, times(1)).existsById(newKitchenId);
            verify(restaurantRepository, never()).save(any(RestaurantEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando novo nome já existe")
        void deveLancarExcecaoQuandoNovoNomeJaExiste() {
            RestaurantModel updateRequest = new RestaurantModel(
                    "Outro Restaurante",
                    kitchenId,
                    true,
                    new BigDecimal("10.00")
            );

            when(restaurantRepository.findByIdWithKitchen(restaurantId)).thenReturn(Optional.of(restaurantEntity));
            when(restaurantRepository.existsByName("Outro Restaurante")).thenReturn(true);

            ResourceAlreadyExistsException exception = assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> restaurantService.update(restaurantId, updateRequest)
            );

            assertNotNull(exception);
            verify(restaurantRepository, never()).save(any(RestaurantEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando há violação de integridade")
        void deveLancarExcecaoQuandoViolacaoIntegridade() {
            RestaurantModel updateRequest = new RestaurantModel(
                    "Restaurante Atualizado",
                    kitchenId,
                    true,
                    new BigDecimal("10.00")
            );

            when(restaurantRepository.findByIdWithKitchen(restaurantId)).thenReturn(Optional.of(restaurantEntity));
            when(restaurantRepository.existsByName("Restaurante Atualizado")).thenReturn(false);
            when(restaurantRepository.save(any(RestaurantEntity.class)))
                    .thenThrow(new DataIntegrityViolationException("Duplicate key"));

            ResourceAlreadyExistsException exception = assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> restaurantService.update(restaurantId, updateRequest)
            );

            assertNotNull(exception);
            verify(restaurantRepository, times(1)).save(any(RestaurantEntity.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando há erro de banco de dados")
        void deveLancarBusinessExceptionQuandoErroBancoDados() {
            when(restaurantRepository.findByIdWithKitchen(restaurantId)).thenReturn(Optional.of(restaurantEntity));
            when(restaurantRepository.save(any(RestaurantEntity.class)))
                    .thenThrow(new DataAccessException("Database error") {});

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> restaurantService.update(restaurantId, restaurantModel)
            );

            assertNotNull(exception);
            assertEquals("DATABASE_ERROR", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("delete() - Deletar restaurante")
    class DeleteRestaurant {

        @Test
        @DisplayName("Deve deletar um restaurante com sucesso")
        void deveDeletarRestauranteComSucesso() {
            when(restaurantRepository.existsById(restaurantId)).thenReturn(true);

            restaurantService.delete(restaurantId);

            verify(restaurantRepository, times(1)).existsById(restaurantId);
            verify(restaurantRepository, times(1)).deleteById(restaurantId);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando restaurante não existe")
        void deveLancarExcecaoQuandoRestauranteNaoExiste() {
            when(restaurantRepository.existsById(restaurantId)).thenReturn(false);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> restaurantService.delete(restaurantId)
            );

            assertNotNull(exception);
            verify(restaurantRepository, times(1)).existsById(restaurantId);
            verify(restaurantRepository, never()).deleteById(any(UUID.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando deleteById lança EmptyResultDataAccessException")
        void deveLancarExcecaoQuandoEmptyResultDataAccessException() {
            when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
            org.mockito.Mockito.doThrow(new EmptyResultDataAccessException(1))
                    .when(restaurantRepository).deleteById(restaurantId);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> restaurantService.delete(restaurantId)
            );

            assertNotNull(exception);
            verify(restaurantRepository, times(1)).deleteById(restaurantId);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando há violação de integridade referencial")
        void deveLancarBusinessExceptionQuandoViolacaoIntegridadeReferencial() {
            when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
            org.mockito.Mockito.doThrow(new DataIntegrityViolationException("Foreign key constraint"))
                    .when(restaurantRepository).deleteById(restaurantId);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> restaurantService.delete(restaurantId)
            );

            assertNotNull(exception);
            assertEquals("DATA_INTEGRITY_VIOLATION", exception.getErrorCode());
            verify(restaurantRepository, times(1)).deleteById(restaurantId);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando há erro de banco de dados")
        void deveLancarBusinessExceptionQuandoErroBancoDados() {
            when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
            org.mockito.Mockito.doThrow(new DataAccessException("Database error") {})
                    .when(restaurantRepository).deleteById(restaurantId);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> restaurantService.delete(restaurantId)
            );

            assertNotNull(exception);
            assertEquals("DATABASE_ERROR", exception.getErrorCode());
        }
    }
}





