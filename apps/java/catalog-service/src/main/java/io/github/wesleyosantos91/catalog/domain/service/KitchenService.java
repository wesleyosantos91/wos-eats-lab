package io.github.wesleyosantos91.catalog.domain.service;

import io.github.wesleyosantos91.catalog.core.mapper.KitchenMapper;
import io.github.wesleyosantos91.catalog.domain.entity.KitchenEntity;
import io.github.wesleyosantos91.catalog.domain.exception.BusinessException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceAlreadyExistsException;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
import io.github.wesleyosantos91.catalog.domain.model.KitchenModel;
import io.github.wesleyosantos91.catalog.domain.repository.KitchenRepository;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KitchenService {

    public static final String DATABASE_ERROR = "DATABASE_ERROR";
    private static final Logger LOGGER = LoggerFactory.getLogger(KitchenService.class);
    private static final String RESOURCE_NAME = "Kitchen";
    public static final String DATA_INTEGRITY_VIOLATION = "DATA_INTEGRITY_VIOLATION";
    public static final String NAME = "name";

    private final KitchenRepository repository;

    public KitchenService(KitchenRepository repository) {
        this.repository = repository;
    }

    @CacheEvict(value = "kitchens", key = "#result.id")
    @Transactional
    public KitchenModel create(KitchenModel model) {
        LOGGER.info("Creating new kitchen with name: {}", model.name());

        try {
            if (repository.existsByName(model.name())) {
                LOGGER.warn("Attempt to create kitchen with existing name: {}", model.name());
                throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, model.name());
            }

            final KitchenEntity kitchenEntity = KitchenMapper.MAPPER.toEntity(model);
            final KitchenEntity savedEntity = repository.save(kitchenEntity);

            LOGGER.info("Kitchen created successfully with id: {}", savedEntity.getId());
            return KitchenMapper.MAPPER.toModel(savedEntity);

        } catch (DataIntegrityViolationException _) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, model.name());

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while creating kitchen. name=" + model.name(), ex, DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public KitchenModel findById(UUID id) {
        LOGGER.debug("Searching for kitchen with id: {}", id);

        try {
            final Optional<KitchenEntity> kitchenOpt = repository.findById(id);

            if (kitchenOpt.isEmpty()) {
                LOGGER.warn("Kitchen not found with id: {}", id);
                throw new ResourceNotFoundException(RESOURCE_NAME, id.toString());
            }

            final KitchenEntity kitchen = kitchenOpt.get();
            LOGGER.debug("Kitchen found with id: {} - name: {}", id, kitchen.getName());
            return KitchenMapper.MAPPER.toModel(kitchen);

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while retrieving kitchen. id=" + id, ex, DATABASE_ERROR);
        }
    }

    @Counted(value = "kitchen.service.search", description = "Number of kitchen search operations")
    @Timed(value = "kitchen.service.search", description = "Time taken for kitchen search operations")
    @Transactional(readOnly = true)
    public Page<KitchenModel> search(KitchenModel queryModel, Pageable pageable) {
        LOGGER.debug("Searching kitchens with query: {} and pageable: {}", queryModel, pageable);

        try {
            final Page<KitchenEntity> result = repository.findByFilters(
                    queryModel.name(),
                    pageable
            );

            LOGGER.debug("Kitchen search completed. Found {} results out of {} total",
                    result.getNumberOfElements(), result.getTotalElements());
            return KitchenMapper.MAPPER.toPageDomain(result);

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while searching kitchens. "
                    + "query=" + queryModel + ", pageable=" + pageable, ex, DATABASE_ERROR);
        }
    }

    @CacheEvict(value = "kitchens", key = "#id")
    @Transactional
    public KitchenModel update(UUID id, KitchenModel model) {
        LOGGER.info("Updating kitchen with id: {}", id);

        try {
            final Optional<KitchenEntity> existingKitchen = repository.findById(id);

            final KitchenEntity current = existingKitchen.orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id.toString()));

            if (!Objects.equals(current.getName(), model.name())
                    && repository.existsByName(model.name())) {
                LOGGER.warn("Attempt to update kitchen name to existing name: {} for id: {}", model.name(), id);
                throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, model.name());
            }

            final KitchenEntity updatedKitchen = KitchenMapper.MAPPER.toEntity(model, current);
            final KitchenEntity savedEntity = repository.save(updatedKitchen);

            LOGGER.info("Kitchen updated successfully with id: {}", id);
            return KitchenMapper.MAPPER.toModel(savedEntity);

        } catch (DataIntegrityViolationException _) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, NAME, model.name());

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while updating kitchen. id=" + id + ", name=" + model.name(), ex, DATABASE_ERROR);
        }
    }

    @CacheEvict(value = "kitchens", key = "#id")
    @Transactional
    public void delete(UUID id) {
        LOGGER.info("Deleting kitchen with id: {}", id);

        try {
            if (!repository.existsById(id)) {
                LOGGER.warn("Attempt to delete non-existing kitchen with id: {}", id);
                throw new ResourceNotFoundException(RESOURCE_NAME, id.toString());
            }

            repository.deleteById(id);
            LOGGER.info("Kitchen deleted successfully with id: {}", id);

        } catch (EmptyResultDataAccessException _) {
            throw new ResourceNotFoundException(RESOURCE_NAME, id.toString());

        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Cannot delete kitchen as it is referenced by other entities. id=" + id, ex, DATA_INTEGRITY_VIOLATION);

        } catch (DataAccessException ex) {
            throw new BusinessException("Database error while deleting kitchen. id=" + id, ex, DATABASE_ERROR);
        }
    }
}
