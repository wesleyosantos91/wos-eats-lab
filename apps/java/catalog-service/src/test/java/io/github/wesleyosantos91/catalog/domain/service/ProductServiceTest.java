package io.github.wesleyosantos91.catalog.domain.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.wesleyosantos91.catalog.domain.port.out.storage.StoragePort;
import io.github.wesleyosantos91.catalog.infrastructure.database.entity.ProductEntity;
import io.github.wesleyosantos91.catalog.infrastructure.database.entity.RestaurantEntity;
import io.github.wesleyosantos91.catalog.domain.exception.BusinessException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceAlreadyExistsException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
import io.github.wesleyosantos91.catalog.domain.model.ProductModel;
import io.github.wesleyosantos91.catalog.infrastructure.database.repository.ProductRepository;
import io.github.wesleyosantos91.catalog.infrastructure.database.repository.RestaurantRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService - Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private StoragePort storagePort;

    @InjectMocks
    private ProductService productService;

    private UUID productId;
    private UUID restaurantId;
    private ProductEntity productEntity;
    private RestaurantEntity restaurantEntity;
    private ProductModel productModel;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();

        restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(restaurantId);
        restaurantEntity.setName("Restaurante A");

        productEntity = new ProductEntity();
        productEntity.setId(productId);
        productEntity.setRestaurant(restaurantEntity);
        productEntity.setName("Produto Teste");
        productEntity.setDescription("Desc");
        productEntity.setPrice(new BigDecimal("20.00"));
        productEntity.setActive(true);
        productEntity.setImageKey(null);

        productModel = new ProductModel(restaurantId, "Produto Teste", "Desc", new BigDecimal("20.00"), true, null);
    }

    @Nested
    @DisplayName("create() - Criar produto")
    class CreateProduct {

        @Test
        @DisplayName("Deve criar um produto com sucesso sem imagem")
        void deveCriarProdutoComSucessoSemImagem() throws Exception {
            when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
            when(repository.existsByNameAndRestaurantId("Produto Teste", restaurantId)).thenReturn(false);
            when(repository.save(any(ProductEntity.class))).thenReturn(productEntity);

            ProductModel result = productService.create(productModel, null);

            assertNotNull(result);
            assertEquals("Produto Teste", result.name());
            verify(restaurantRepository, times(1)).existsById(restaurantId);
            verify(repository, times(1)).save(any(ProductEntity.class));
            verify(storagePort, never()).uploadFile(any(MultipartFile.class));
        }

        @Test
        @DisplayName("Deve criar um produto com imagem e salvar imageKey")
        void deveCriarProdutoComImagem() throws Exception {
            when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
            when(repository.existsByNameAndRestaurantId("Produto Teste", restaurantId)).thenReturn(false);
            when(repository.save(any(ProductEntity.class))).thenReturn(productEntity);
            when(storagePort.uploadFile(any(MultipartFile.class))).thenReturn("image-key-123");

            MockMultipartFile file = new MockMultipartFile("image", "file.png", "image/png", "bytes".getBytes());

            ProductModel result = productService.create(productModel, file);

            assertNotNull(result);
            assertEquals("Produto Teste", result.name());
            assertEquals("image-key-123", result.imageKey());
            verify(storagePort, times(1)).uploadFile(any(MultipartFile.class));
            verify(repository, times(1)).save(any(ProductEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando restaurante não existe")
        void deveLancarQuandoRestauranteNaoExiste() {
            when(restaurantRepository.existsById(restaurantId)).thenReturn(false);

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.create(productModel, null));

            assertNotNull(ex);
            verify(repository, never()).save(any(ProductEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando nome já existe")
        void deveLancarQuandoNomeExiste() {
            when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
            when(repository.existsByNameAndRestaurantId("Produto Teste", restaurantId)).thenReturn(true);

            ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> productService.create(productModel, null));

            assertNotNull(ex);
            verify(repository, never()).save(any(ProductEntity.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessException em caso de erro de banco")
        void deveLancarBusinessExceptionQuandoErroBanco() {
            when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
            when(repository.existsByNameAndRestaurantId("Produto Teste", restaurantId)).thenReturn(false);
            when(repository.save(any(ProductEntity.class))).thenThrow(new DataAccessException("DB error") {});

            BusinessException ex = assertThrows(BusinessException.class, () -> productService.create(productModel, null));

            assertNotNull(ex);
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando DataIntegrityViolationException")
        void deveLancarResourceAlreadyExistsQuandoDataIntegrityViolation() {
            when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
            when(repository.existsByNameAndRestaurantId("Produto Teste", restaurantId)).thenReturn(false);
            when(repository.save(any(ProductEntity.class))).thenThrow(new DataIntegrityViolationException("Unique constraint"));

            ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> productService.create(productModel, null));

            assertNotNull(ex);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando IOException no upload")
        void deveLancarBusinessQuandoIOExceptionUpload() throws Exception {
            when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
            when(repository.existsByNameAndRestaurantId("Produto Teste", restaurantId)).thenReturn(false);
            when(repository.save(any(ProductEntity.class))).thenReturn(productEntity);
            MockMultipartFile file = new MockMultipartFile("image", "file.png", "image/png", "bytes".getBytes());
            when(storagePort.uploadFile(any(MultipartFile.class))).thenThrow(new IOException("Upload failed"));

            BusinessException ex = assertThrows(BusinessException.class, () -> productService.create(productModel, file));

            assertNotNull(ex);
        }

        @Test
        @DisplayName("Deve criar produto com imagem vazia")
        void deveCriarComImagemVazia() throws Exception {
            when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
            when(repository.existsByNameAndRestaurantId("Produto Teste", restaurantId)).thenReturn(false);
            when(repository.save(any(ProductEntity.class))).thenReturn(productEntity);

            MockMultipartFile emptyFile = new MockMultipartFile("image", "empty.png", "image/png", new byte[0]);

            ProductModel result = productService.create(productModel, emptyFile);

            assertNotNull(result);
            verify(storagePort, never()).uploadFile(any(MultipartFile.class));
        }
    }

    @Nested
    @DisplayName("findById() - Buscar produto por ID")
    class FindById {

        @Test
        @DisplayName("Deve retornar produto quando existe")
        void deveRetornarQuandoExiste() {
            when(repository.findById(productId)).thenReturn(Optional.of(productEntity));

            ProductModel result = productService.findById(productId);

            assertNotNull(result);
            assertEquals(productId, result.id());
            verify(repository, times(1)).findById(productId);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando nao existe")
        void deveLancarQuandoNaoExiste() {
            when(repository.findById(productId)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.findById(productId));

            assertNotNull(ex);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando erro DB")
        void deveLancarBusinessQuandoErroDB() {
            when(repository.findById(productId)).thenThrow(new DataAccessException("DB") {});

            BusinessException ex = assertThrows(BusinessException.class, () -> productService.findById(productId));

            assertNotNull(ex);
        }
    }

    @Nested
    @DisplayName("search() - Buscar produtos com filtros")
    class SearchProducts {

        @Test
        @DisplayName("Deve retornar pagina de produtos")
        void deveRetornarPagina() {
            Pageable pageable = PageRequest.of(0, 10);
            List<ProductEntity> list = new ArrayList<>();
            list.add(productEntity);
            Page<ProductEntity> page = new PageImpl<>(list, pageable, 1);

            ProductModel query = new ProductModel(restaurantId, null, null, null, null, null);

            when(repository.findByFilters(restaurantId, null, null, null, null, pageable)).thenReturn(page);

            Page<ProductModel> result = productService.search(query, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(repository, times(1)).findByFilters(restaurantId, null, null, null, null, pageable);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando erro DB")
        void deveLancarBusinessQuandoErroDB() {
            Pageable pageable = PageRequest.of(0, 10);
            ProductModel query = new ProductModel(restaurantId, null, null, null, null, null);

            when(repository.findByFilters(any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenThrow(new DataAccessException("DB") {});

            BusinessException ex = assertThrows(BusinessException.class, () -> productService.search(query, pageable));

            assertNotNull(ex);
        }
    }

    @Nested
    @DisplayName("update() - Atualizar produto")
    class UpdateProduct {

        @Test
        @MockitoSettings(strictness = Strictness.LENIENT)
        @DisplayName("Deve atualizar produto com sucesso e trocar imagem")
        void deveAtualizarComSucessoETrocarImagem() throws Exception {
            MockMultipartFile file = new MockMultipartFile("image", "file.png", "image/png", "newbytes".getBytes());

            ProductEntity current = new ProductEntity();
            current.setId(productId);
            current.setRestaurant(restaurantEntity);
            current.setName("Produto Teste");
            current.setPrice(new BigDecimal("20.00"));
            current.setActive(true);
            current.setImageKey("old-key");

            when(repository.findByIdWithRestaurant(productId)).thenReturn(Optional.of(current));
            when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
            when(repository.existsByNameAndRestaurantId("Produto Teste", restaurantId)).thenReturn(false);

            ProductEntity saved = new ProductEntity();
            saved.setId(productId);
            saved.setRestaurant(restaurantEntity);
            saved.setName("Produto Teste");
            saved.setPrice(new BigDecimal("20.00"));
            saved.setImageKey("new-key");

            org.mockito.Mockito.lenient().when(storagePort.uploadFile(any(MultipartFile.class))).thenReturn("new-key");
            org.mockito.Mockito.lenient().when(repository.save(any(ProductEntity.class))).thenReturn(saved);

            ProductModel result = productService.update(productId, productModel, file);

            assertNotNull(result);
            assertEquals("new-key", result.imageKey());
            verify(storagePort, times(1)).deleteFile("old-key");
            verify(repository, times(1)).save(any(ProductEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando produto nao existe")
        void deveLancarQuandoNaoExiste() {
            when(repository.findByIdWithRestaurant(productId)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.update(productId, productModel, null));

            assertNotNull(ex);
            verify(repository, never()).save(any(ProductEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando nova restaurantId nao existe")
        void deveLancarQuandoNovaRestaurantNaoExiste() {
            UUID newRest = UUID.randomUUID();
            ProductModel modelWithNewRest = new ProductModel(newRest, "Produto Teste", "Desc", new BigDecimal("20.00"), true, null);

            ProductEntity current = new ProductEntity();
            current.setId(productId);
            current.setRestaurant(restaurantEntity);

            when(repository.findByIdWithRestaurant(productId)).thenReturn(Optional.of(current));
            when(restaurantRepository.existsById(newRest)).thenReturn(false);

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.update(productId, modelWithNewRest, null));

            assertNotNull(ex);
            verify(repository, never()).save(any(ProductEntity.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando novo nome já existe")
        void deveLancarQuandoNomeJaExiste() {
            ProductEntity current = new ProductEntity();
            current.setId(productId);
            current.setRestaurant(restaurantEntity);
            current.setName("Produto Teste");

            when(repository.findByIdWithRestaurant(productId)).thenReturn(Optional.of(current));
            when(repository.existsByNameAndRestaurantId("Outro", restaurantId)).thenReturn(true);

            ProductModel model = new ProductModel(restaurantId, "Outro", "Desc", new BigDecimal("20.00"), true, null);

            ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> productService.update(productId, model, null));

            assertNotNull(ex);
            verify(repository, never()).save(any(ProductEntity.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando erro DB ao salvar")
        void deveLancarBusinessQuandoErroDB() {
            ProductEntity current = new ProductEntity();
            current.setId(productId);
            current.setRestaurant(restaurantEntity);

            when(repository.findByIdWithRestaurant(productId)).thenReturn(Optional.of(current));
            when(repository.save(any(ProductEntity.class))).thenThrow(new DataAccessException("DB") {});

            BusinessException ex = assertThrows(BusinessException.class, () -> productService.update(productId, productModel, null));

            assertNotNull(ex);
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando DataIntegrityViolationException no update")
        void deveLancarResourceAlreadyExistsQuandoDataIntegrityViolationUpdate() {
            ProductEntity current = new ProductEntity();
            current.setId(productId);
            current.setRestaurant(restaurantEntity);

            when(repository.findByIdWithRestaurant(productId)).thenReturn(Optional.of(current));
            when(repository.save(any(ProductEntity.class))).thenThrow(new DataIntegrityViolationException("Unique constraint"));

            ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> productService.update(productId, productModel, null));

            assertNotNull(ex);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando IOException no upload durante update")
        void deveLancarBusinessQuandoIOExceptionUploadUpdate() throws Exception {
            ProductEntity current = new ProductEntity();
            current.setId(productId);
            current.setRestaurant(restaurantEntity);

            when(repository.findByIdWithRestaurant(productId)).thenReturn(Optional.of(current));
            MockMultipartFile file = new MockMultipartFile("image", "file.png", "image/png", "bytes".getBytes());
            when(storagePort.uploadFile(any(MultipartFile.class))).thenThrow(new IOException("Upload failed"));

            BusinessException ex = assertThrows(BusinessException.class, () -> productService.update(productId, productModel, file));

            assertNotNull(ex);
        }

        @Test
        @DisplayName("Deve atualizar produto sem imagem")
        void deveAtualizarSemImagem() throws Exception {
            ProductEntity current = new ProductEntity();
            current.setId(productId);
            current.setRestaurant(restaurantEntity);
            current.setName("Produto Teste");
            current.setImageKey("old-key");

            when(repository.findByIdWithRestaurant(productId)).thenReturn(Optional.of(current));
            when(repository.save(any(ProductEntity.class))).thenReturn(current);

            ProductModel result = productService.update(productId, productModel, null);

            assertNotNull(result);
            assertEquals("old-key", result.imageKey());
            verify(storagePort, never()).uploadFile(any(MultipartFile.class));
            verify(storagePort, never()).deleteFile(anyString());
        }

        @Test
        @DisplayName("Deve atualizar produto com nome diferente e nao existe")
        void deveAtualizarComNomeDiferenteENaoExiste() {
            ProductEntity current = new ProductEntity();
            current.setId(productId);
            current.setRestaurant(restaurantEntity);
            current.setName("Produto Teste");

            when(repository.findByIdWithRestaurant(productId)).thenReturn(Optional.of(current));
            when(repository.existsByNameAndRestaurantId("Novo Nome", restaurantId)).thenReturn(false);
            when(repository.save(any(ProductEntity.class))).thenReturn(current);

            ProductModel model = new ProductModel(restaurantId, "Novo Nome", "Desc", new BigDecimal("20.00"), true, null);

            ProductModel result = productService.update(productId, model, null);

            assertNotNull(result);
            verify(repository, times(1)).existsByNameAndRestaurantId("Novo Nome", restaurantId);
        }

        @Test
        @DisplayName("Deve atualizar produto com imagem quando nao tinha imagem")
        void deveAtualizarComImagemQuandoNaoTinha() throws Exception {
            ProductEntity current = new ProductEntity();
            current.setId(productId);
            current.setRestaurant(restaurantEntity);
            current.setName("Produto Teste");
            current.setImageKey(null); // no old image

            when(repository.findByIdWithRestaurant(productId)).thenReturn(Optional.of(current));
            when(repository.save(any(ProductEntity.class))).thenReturn(current);

            MockMultipartFile file = new MockMultipartFile("image", "file.png", "image/png", "newbytes".getBytes());

            ProductModel result = productService.update(productId, productModel, file);

            assertNotNull(result);
            verify(storagePort, times(1)).uploadFile(any(MultipartFile.class));
            verify(storagePort, never()).deleteFile(anyString()); // since old was null
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando restaurante mudou e nome já existe no novo restaurante")
        void deveLancarQuandoRestauranteMudouENomeJaExiste() {
            UUID newRest = UUID.randomUUID();
            ProductModel modelWithNewRest = new ProductModel(newRest, "Produto Teste", "Desc", new BigDecimal("20.00"), true, null);

            ProductEntity current = new ProductEntity();
            current.setId(productId);
            current.setRestaurant(restaurantEntity);
            current.setName("Produto Teste");

            when(repository.findByIdWithRestaurant(productId)).thenReturn(Optional.of(current));
            when(restaurantRepository.existsById(newRest)).thenReturn(true);
            when(repository.existsByNameAndRestaurantId("Produto Teste", newRest)).thenReturn(true);

            ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> productService.update(productId, modelWithNewRest, null));

            assertNotNull(ex);
            verify(repository, never()).save(any(ProductEntity.class));
        }
    }

    @Nested
    @DisplayName("delete() - Deletar produto")
    class DeleteProduct {

        @Test
        @DisplayName("Deve deletar com sucesso")
        void deveDeletarComSucesso() {
            when(repository.findById(productId)).thenReturn(Optional.of(productEntity));
            productEntity.setImageKey("img-key");

            productService.delete(productId);

            verify(repository, times(1)).delete(productEntity);
            verify(storagePort, times(1)).deleteFile("img-key");
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando nao existe")
        void deveLancarQuandoNaoExiste() {
            when(repository.findById(productId)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.delete(productId));

            assertNotNull(ex);
            verify(repository, never()).delete(any(ProductEntity.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando violacao integridade")
        void deveLancarQuandoViolacaoIntegridade() {
            when(repository.findById(productId)).thenReturn(Optional.of(productEntity));
            org.mockito.Mockito.doThrow(new DataIntegrityViolationException("FK"))
                    .when(repository).delete(productEntity);

            BusinessException ex = assertThrows(BusinessException.class, () -> productService.delete(productId));

            assertNotNull(ex);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando erro DB")
        void deveLancarQuandoErroDB() {
            when(repository.findById(productId)).thenReturn(Optional.of(productEntity));
            org.mockito.Mockito.doThrow(new DataAccessException("DB") {})
                    .when(repository).delete(productEntity);

            BusinessException ex = assertThrows(BusinessException.class, () -> productService.delete(productId));

            assertNotNull(ex);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando EmptyResultDataAccessException")
        void deveLancarQuandoEmptyResult() {
            when(repository.findById(productId)).thenReturn(Optional.of(productEntity));
            productEntity.setImageKey("img-key");
            org.mockito.Mockito.doThrow(new org.springframework.dao.EmptyResultDataAccessException(1))
                    .when(repository).delete(productEntity);

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.delete(productId));

            assertNotNull(ex);
            verify(storagePort, never()).deleteFile(anyString());
        }
    }

    @Nested
    @DisplayName("deleteImage() - Deletar imagem do produto")
    class DeleteImage {

        @Test
        @DisplayName("Deve deletar imagem com sucesso")
        void deveDeletarImagemComSucesso() {
            productEntity.setImageKey("img-key");
            when(repository.findById(productId)).thenReturn(Optional.of(productEntity));

            ProductModel result = productService.deleteImage(productId);

            assertNotNull(result);
            assertEquals(null, result.imageKey());
            verify(storagePort, times(1)).deleteFile("img-key");
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando imagem nao existe")
        void deveLancarQuandoImagemNaoExiste() {
            productEntity.setImageKey(null);
            when(repository.findById(productId)).thenReturn(Optional.of(productEntity));

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.deleteImage(productId));

            assertNotNull(ex);
            verify(storagePort, never()).deleteFile(anyString());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando produto nao existe")
        void deveLancarQuandoProdutoNaoExiste() {
            when(repository.findById(productId)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.deleteImage(productId));

            assertNotNull(ex);
        }
    }

    @Nested
    @DisplayName("getImage() - Baixar imagem do produto")
    class GetImage {

        @Test
        @DisplayName("Deve retornar bytes da imagem")
        void deveRetornarBytes() {
            productEntity.setImageKey("img-key");
            when(repository.findById(productId)).thenReturn(Optional.of(productEntity));
            byte[] bytes = "content".getBytes();
            when(storagePort.downloadFile("img-key")).thenReturn(bytes);

            ProductModel result = productService.getImage(productId);

            assertNotNull(result);
            assertEquals("img-key", result.imageKey());
            assertArrayEquals(bytes, result.image());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando imagem nao existe")
        void deveLancarQuandoImagemNaoExiste() {
            productEntity.setImageKey(null);
            when(repository.findById(productId)).thenReturn(Optional.of(productEntity));

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.getImage(productId));

            assertNotNull(ex);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando produto nao existe")
        void deveLancarQuandoProdutoNaoExiste() {
            when(repository.findById(productId)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.getImage(productId));

            assertNotNull(ex);
        }
    }
}
