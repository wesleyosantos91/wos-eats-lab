package io.github.wesleyosantos91.catalog.domain.service;

import io.github.wesleyosantos91.catalog.core.annotation.Adapter;
import io.github.wesleyosantos91.catalog.core.mapper.RestaurantMapper;
import io.github.wesleyosantos91.catalog.core.port.in.restaurant.RestaurantServicePort;
import io.github.wesleyosantos91.catalog.domain.entity.RestaurantEntity;
import io.github.wesleyosantos91.catalog.domain.exception.BusinessException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceAlreadyExistsException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
import io.github.wesleyosantos91.catalog.domain.model.RestaurantModel;
import io.github.wesleyosantos91.catalog.domain.repository.KitchenRepository;
import io.github.wesleyosantos91.catalog.domain.repository.RestaurantRepository;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Adapter(type = Adapter.AdapterType.INBOUND, description = "Restaurant Service Adapter")
public class RestaurantService implements RestaurantServicePort {

    public static final String DATABASE_ERROR = "DATABASE_ERROR";
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantService.class);
    private static final String RESOURCE_NAME = "Restaurant";
    private static final String KITCHEN_RESOURCE_NAME = "Kitchen";
    public static final String DATA_INTEGRITY_VIOLATION = "DATA_INTEGRITY_VIOLATION";
    public static final String NAME = "name";

    private final RestaurantRepository repository;
    private final KitchenRepository kitchenRepository;

    public RestaurantService(RestaurantRepository repository, KitchenRepository kitchenRepository) {
        this.repository = repository;
        this.kitchenRepository = kitchenRepository;
    }

    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public RestaurantModel create(RestaurantModel model) {
        LOGGER.info("Creating new restaurant with name: {} for kitchen: {}", model.name(), model.kitchenId());

        try {
            if (!kitchenRepository.existsById(model.kitchenId())) {
                throw new ResourceNotFoundException(KITCHEN_RESOURCE_NAME, model.kitchenId().toString());
            }

            if (repository.existsByName(model.name())) {
                throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, model.name());
            }

            final RestaurantEntity restaurantEntity = RestaurantMapper.MAPPER.toEntity(model);
            final RestaurantEntity savedEntity = repository.save(restaurantEntity);

            LOGGER.info("Restaurant created successfully with id: {}", savedEntity.getId());
            return RestaurantMapper.MAPPER.toModel(savedEntity);

        } catch (DataIntegrityViolationException _) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, model.name());
        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while creating restaurant. name=" + model.name(), ex, DATABASE_ERROR);
        }
    }

    @Cacheable(value = "restaurants", key = "#id")
    @Transactional(readOnly = true)
    public RestaurantModel findById(UUID id) {
        LOGGER.debug("Searching for restaurant with id: {}", id);

        try {
            final Optional<RestaurantEntity> restaurantOpt = repository.findById(id);

            if (restaurantOpt.isEmpty()) {
                throw new ResourceNotFoundException(RESOURCE_NAME, id.toString());
            }

            final RestaurantEntity restaurant = restaurantOpt.get();
            LOGGER.debug("Restaurant found with id: {} - name: {}", id, restaurant.getName());
            return RestaurantMapper.MAPPER.toModel(restaurant);

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while retrieving restaurant. id=" + id, ex, DATABASE_ERROR);
        }
    }

    @Counted(value = "restaurant.service.search", description = "Number of restaurant search operations")
    @Timed(value = "restaurant.service.search", description = "Time taken for restaurant search operations")
    @Transactional(readOnly = true)
    public Page<RestaurantModel> search(RestaurantModel queryModel, Pageable pageable) {
        LOGGER.debug("Searching restaurants with query: {} and pageable: {}", queryModel, pageable);

        try {
            final Page<RestaurantEntity> result = repository.findByFilters(
                    queryModel.name(),
                    queryModel.kitchenId(),
                    queryModel.active(),
                    queryModel.minDeliveryFee(),
                    queryModel.maxDeliveryFee(),
                    pageable
            );

            LOGGER.debug("Restaurant search completed. Found {} results out of {} total",
                    result.getNumberOfElements(), result.getTotalElements());
            return RestaurantMapper.MAPPER.toPageDomain(result);

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while searching restaurants. "
                    + "query=" + queryModel + ", pageable=" + pageable, ex, DATABASE_ERROR);
        }
    }

    @Transactional
    @CacheEvict(value = "restaurants", key = "#id")
    public RestaurantModel update(UUID id, RestaurantModel model) {
        LOGGER.info("Updating restaurant with id: {}", id);

        try {
            final Optional<RestaurantEntity> existingRestaurant = repository.findByIdWithKitchen(id);
            final RestaurantEntity current = existingRestaurant.orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id.toString()));

            if (!Objects.equals(current.getKitchen().getId(), model.kitchen().id()) && !kitchenRepository.existsById(model.kitchen().id())) {
                throw new ResourceNotFoundException(KITCHEN_RESOURCE_NAME, model.kitchen().id().toString());
            }


            if (!Objects.equals(current.getName(), model.name())
                    && repository.existsByName(model.name())) {
                throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, model.name());
            }

            final RestaurantEntity updatedRestaurant = RestaurantMapper.MAPPER.toEntity(model, current);
            final RestaurantEntity savedEntity = repository.save(updatedRestaurant);

            LOGGER.info("Restaurant updated successfully with id: {}", id);
            return RestaurantMapper.MAPPER.toModel(savedEntity);

        } catch (DataIntegrityViolationException _) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, model.name());
        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while updating restaurant. id=" + id + ", name=" + model.name(), ex, DATABASE_ERROR);
        }
    }

    @Transactional
    @CacheEvict(value = "restaurants", key = "#id")
    public void delete(UUID id) {
        LOGGER.info("Deleting restaurant with id: {}", id);

        try {
            if (!repository.existsById(id)) {
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
