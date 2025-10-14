package io.github.wesleyosantos91.catalog.domain.service;

import io.github.wesleyosantos91.catalog.api.v1.request.RestaurantQueryRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.RestaurantRequest;
import io.github.wesleyosantos91.catalog.core.mapper.RestaurantMapper;
import io.github.wesleyosantos91.catalog.domain.entity.RestaurantEntity;
import io.github.wesleyosantos91.catalog.domain.exception.BusinessException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceAlreadyExistsException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
import io.github.wesleyosantos91.catalog.domain.repository.KitchenRepository;
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
public class RestaurantService {

    public static final String DATABASE_ERROR = "DATABASE_ERROR";
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantService.class);
    private static final String RESOURCE_NAME = "Restaurant";
    public static final String UNEXPECTED_ERROR = "UNEXPECTED_ERROR";
    public static final String DATA_INTEGRITY_VIOLATION = "DATA_INTEGRITY_VIOLATION";
    public static final String NAME = "name";

    private final RestaurantRepository repository;
    private final KitchenRepository kitchenRepository;

    public RestaurantService(RestaurantRepository repository, KitchenRepository kitchenRepository) {
        this.repository = repository;
        this.kitchenRepository = kitchenRepository;
    }

    @Transactional
    public RestaurantEntity create(RestaurantRequest request) {
        LOGGER.info("Creating new restaurant with name: {} for kitchen: {}", request.name(), request.kitchenId());

        try {
            if (!kitchenRepository.existsById(request.kitchenId())) {
                LOGGER.warn("Attempt to create restaurant for non-existing kitchen: {}", request.kitchenId());
                throw new ResourceNotFoundException("Kitchen", request.kitchenId().toString());
            }

            if (repository.existsByName(request.name())) {
                LOGGER.warn("Attempt to create restaurant with existing name: {}", request.name());
                throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, request.name());
            }

            final RestaurantEntity restaurantEntity = RestaurantMapper.MAPPER.toEntity(request);
            final RestaurantEntity savedEntity = repository.save(restaurantEntity);

            LOGGER.info("Restaurant created successfully with id: {}", savedEntity.getId());
            return savedEntity;

        } catch (DataIntegrityViolationException _) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, request.name());

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while creating restaurant. name=" + request.name(), ex, DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public RestaurantEntity findById(UUID id) {
        LOGGER.debug("Searching for restaurant with id: {}", id);

        try {
            final Optional<RestaurantEntity> restaurantOpt = repository.findById(id);

            if (restaurantOpt.isEmpty()) {
                LOGGER.warn("Restaurant not found with id: {}", id);
                throw new ResourceNotFoundException(RESOURCE_NAME, id.toString());
            }

            final RestaurantEntity restaurant = restaurantOpt.get();
            LOGGER.debug("Restaurant found with id: {} - name: {}", id, restaurant.getName());
            return restaurant;

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while retrieving restaurant. id=" + id, ex, DATABASE_ERROR);
        }
    }

    @Counted(value = "restaurant.service.search", description = "Number of restaurant search operations")
    @Timed(value = "restaurant.service.search", description = "Time taken for restaurant search operations")
    @Transactional(readOnly = true)
    public Page<RestaurantEntity> search(RestaurantQueryRequest queryRequest, Pageable pageable) {
        LOGGER.debug("Searching restaurants with query: {} and pageable: {}", queryRequest, pageable);

        try {
            final Page<RestaurantEntity> result = repository.findByFilters(
                    queryRequest.name(),
                    queryRequest.kitchenId(),
                    queryRequest.active(),
                    queryRequest.minDeliveryFee(),
                    queryRequest.maxDeliveryFee(),
                    pageable
            );

            LOGGER.debug("Restaurant search completed. Found {} results out of {} total",
                    result.getNumberOfElements(), result.getTotalElements());
            return result;

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while searching restaurants. "
                    + "query=" + queryRequest + ", pageable=" + pageable, ex, DATABASE_ERROR);
        }
    }

    @Transactional
    public RestaurantEntity update(UUID id, RestaurantRequest request) {
        LOGGER.info("Updating restaurant with id: {}", id);

        try {
            final Optional<RestaurantEntity> existingRestaurant = repository.findById(id);
            final RestaurantEntity current = existingRestaurant.orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id.toString()));

            // Validate kitchen exists if it's being changed
            if (!Objects.equals(current.getKitchen().getId(), request.kitchenId())) {
                if (!kitchenRepository.existsById(request.kitchenId())) {
                    LOGGER.warn("Attempt to update restaurant to non-existing kitchen: {}", request.kitchenId());
                    throw new ResourceNotFoundException("Kitchen", request.kitchenId().toString());
                }
            }

            // Check for duplicate restaurant name (only if name is being changed)
            if (!Objects.equals(current.getName(), request.name())
                    && repository.existsByName(request.name())) {
                LOGGER.warn("Attempt to update restaurant name to existing name: {} for id: {}", request.name(), id);
                throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, request.name());
            }

            final RestaurantEntity updatedRestaurant = RestaurantMapper.MAPPER.toEntity(request, current);
            final RestaurantEntity savedEntity = repository.save(updatedRestaurant);

            LOGGER.info("Restaurant updated successfully with id: {}", id);
            return savedEntity;

        } catch (DataIntegrityViolationException _) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, request.name());

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while updating restaurant. id=" + id + ", name=" + request.name(), ex, DATABASE_ERROR);
        }
    }

    @Transactional
    public void delete(UUID id) {
        LOGGER.info("Deleting restaurant with id: {}", id);

        try {
            if (!repository.existsById(id)) {
                LOGGER.warn("Attempt to delete non-existing restaurant with id: {}", id);
                throw new ResourceNotFoundException(RESOURCE_NAME, id.toString());
            }

            repository.deleteById(id);
            LOGGER.info("Restaurant deleted successfully with id: {}", id);

        } catch (EmptyResultDataAccessException _) {
            throw new ResourceNotFoundException(RESOURCE_NAME, id.toString());

        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Cannot delete restaurant as it is referenced by other entities. id=" + id, ex, DATA_INTEGRITY_VIOLATION);

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while deleting restaurant. id=" + id, ex, DATABASE_ERROR);
        }
    }
}
