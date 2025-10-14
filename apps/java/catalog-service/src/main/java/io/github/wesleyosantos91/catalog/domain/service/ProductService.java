package io.github.wesleyosantos91.catalog.domain.service;

import io.github.wesleyosantos91.catalog.api.v1.request.ProductQueryRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.ProductRequest;
import io.github.wesleyosantos91.catalog.core.mapper.ProductMapper;
import io.github.wesleyosantos91.catalog.domain.entity.ProductEntity;
import io.github.wesleyosantos91.catalog.domain.exception.BusinessException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceAlreadyExistsException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
import io.github.wesleyosantos91.catalog.domain.repository.ProductRepository;
import io.github.wesleyosantos91.catalog.domain.repository.RestaurantRepository;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    public static final String DATABASE_ERROR = "DATABASE_ERROR";
    public static final String NAME = "name";
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private static final String RESOURCE_NAME = "Product";
    public static final String UNEXPECTED_ERROR = "UNEXPECTED_ERROR";
    public static final String DATA_INTEGRITY_VIOLATION = "DATA_INTEGRITY_VIOLATION";

    private final ProductRepository repository;
    private final RestaurantRepository restaurantRepository;

    public ProductService(ProductRepository repository, RestaurantRepository restaurantRepository) {
        this.repository = repository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public ProductEntity create(ProductRequest request) {
        LOGGER.info("Creating new product with name: {} for restaurant: {}", request.name(), request.restaurantId());

        try {
            if (!restaurantRepository.existsById(request.restaurantId())) {
                LOGGER.warn("Attempt to create product for non-existing restaurant: {}", request.restaurantId());
                throw new ResourceNotFoundException("Restaurant", request.restaurantId().toString());
            }

            if (repository.existsByNameAndRestaurantId(request.name(), request.restaurantId())) {
                LOGGER.warn("Attempt to create product with existing name: {} for restaurant: {}", request.name(), request.restaurantId());
                throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, request.name());
            }

            final ProductEntity productEntity = ProductMapper.MAPPER.toEntity(request);
            final ProductEntity savedEntity = repository.save(productEntity);

            LOGGER.info("Product created successfully with id: {}", savedEntity.getId());
            return savedEntity;

        } catch (DataIntegrityViolationException _) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, request.name());

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while creating product. name=" + request.name(), ex, DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ProductEntity findById(UUID id) {
        LOGGER.debug("Searching for product with id: {}", id);

        try {
            final Optional<ProductEntity> productOpt = repository.findById(id);

            if (productOpt.isEmpty()) {
                LOGGER.warn("Product not found with id: {}", id);
                throw new ResourceNotFoundException(RESOURCE_NAME, id.toString());
            }

            final ProductEntity product = productOpt.get();
            LOGGER.debug("Product found with id: {} - name: {}", id, product.getName());
            return product;

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while retrieving product. id=" + id, ex, DATABASE_ERROR);
        }
    }

    @Counted(value = "product.service.search", description = "Number of product search operations")
    @Timed(value = "product.service.search", description = "Time taken for product search operations")
    @Transactional(readOnly = true)
    public Page<ProductEntity> search(ProductQueryRequest queryRequest, Pageable pageable) {
        LOGGER.debug("Searching products with query: {} and pageable: {}", queryRequest, pageable);

        try {
            final Page<ProductEntity> result = repository.findByFilters(
                    queryRequest.restaurantId(),
                    queryRequest.name(),
                    queryRequest.minPrice(),
                    queryRequest.maxPrice(),
                    queryRequest.active(),
                    pageable
            );

            LOGGER.debug("Product search completed. Found {} results out of {} total",
                    result.getNumberOfElements(), result.getTotalElements());
            return result;

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while searching products. "
                    + "query=" + queryRequest + ", pageable=" + pageable, ex, DATABASE_ERROR);
        }
    }

    @Transactional
    public ProductEntity update(UUID id, ProductRequest request) {
        LOGGER.info("Updating product with id: {}", id);

        try {
            final Optional<ProductEntity> existingProduct = repository.findById(id);
            final ProductEntity current = existingProduct.orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id.toString()));

            if (!Objects.equals(current.getRestaurant().getId(), request.restaurantId())) {
                if (!restaurantRepository.existsById(request.restaurantId())) {
                    LOGGER.warn("Attempt to update product to non-existing restaurant: {}", request.restaurantId());
                    throw new ResourceNotFoundException("Restaurant", request.restaurantId().toString());
                }
            }

            if ((!Objects.equals(current.getName(), request.name()) || !Objects.equals(current.getRestaurant().getId(), request.restaurantId()))
                    && repository.existsByNameAndRestaurantId(request.name(), request.restaurantId())) {
                LOGGER.warn("Attempt to update product name to existing name: {} for restaurant: {} "
                        + "and id: {}", request.name(), request.restaurantId(), id);
                throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, request.name());
            }

            final ProductEntity updatedProduct = ProductMapper.MAPPER.toEntity(request, current);
            final ProductEntity savedEntity = repository.save(updatedProduct);

            LOGGER.info("Product updated successfully with id: {}", id);
            return savedEntity;

        } catch (DataIntegrityViolationException _) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, request.name());

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while updating product. id=" + id + ", name=" + request.name(), ex, DATABASE_ERROR);
        }
    }

    @Transactional
    public void delete(UUID id) {
        LOGGER.info("Deleting product with id: {}", id);

        try {
            if (!repository.existsById(id)) {
                LOGGER.warn("Attempt to delete non-existing product with id: {}", id);
                throw new ResourceNotFoundException(RESOURCE_NAME, id.toString());
            }

            repository.deleteById(id);
            LOGGER.info("Product deleted successfully with id: {}", id);

        } catch (EmptyResultDataAccessException _) {
            throw new ResourceNotFoundException(RESOURCE_NAME, id.toString());

        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Cannot delete product as it is referenced by other entities. id=" + id, ex, DATA_INTEGRITY_VIOLATION);

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while deleting product. id=" + id, ex, DATABASE_ERROR);
        }
    }
}
