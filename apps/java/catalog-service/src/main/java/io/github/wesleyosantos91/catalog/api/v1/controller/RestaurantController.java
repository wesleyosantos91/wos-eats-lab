package io.github.wesleyosantos91.catalog.api.v1.controller;

import static io.github.wesleyosantos91.catalog.core.mapper.RestaurantMapper.MAPPER;

import io.github.wesleyosantos91.catalog.api.v1.request.RestaurantQueryRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.RestaurantRequest;
import io.github.wesleyosantos91.catalog.api.v1.response.RestaurantResponse;
import io.github.wesleyosantos91.catalog.core.validation.Groups;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
import io.github.wesleyosantos91.catalog.domain.service.RestaurantService;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/restaurants")
public record RestaurantController(RestaurantService service) {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);

    @PostMapping
    public ResponseEntity<RestaurantResponse> create(@Validated(Groups.Create.class) @RequestBody RestaurantRequest request) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'create restaurant'");
        final var model = MAPPER.toModel(request);
        final var createdModel = service.create(model);
        final var response = MAPPER.toResponse(createdModel);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'create restaurant {}' in {} ms", response, stopWatch.getTotalTimeMillis());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RestaurantResponse> getById(@PathVariable UUID id) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'getById restaurant' with id {}", id);
        final var model = service.findById(id);
        final var response = MAPPER.toResponse(model);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'getById restaurant' {} in {} ms", response, stopWatch.getTotalTimeMillis());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<PagedModel<RestaurantResponse>> search(@ModelAttribute RestaurantQueryRequest query, Pageable page) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.info("Function started 'find restaurant'");
        final var queryModel = MAPPER.toQueryModel(query);
        final var pageModel = service.search(queryModel, page);
        stopWatch.stop();
        LOGGER.info("finished function with restaurant 'find restaurant' in {} ms", stopWatch.getTotalTimeMillis());

        return ResponseEntity.ok().body(new PagedModel<>(MAPPER.toPageResponse(pageModel)));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<RestaurantResponse> update(@PathVariable UUID id,
                                                     @Validated(Groups.Update.class)
                                                     @RequestBody RestaurantRequest request) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'update restaurant'");
        final var model = MAPPER.toModel(request);
        final var updatedModel = service.update(id, model);
        final var response = MAPPER.toResponse(updatedModel);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'update restaurant' {} in {} ms", response, stopWatch.getTotalTimeMillis());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'delete restaurant' with id {}", id);
        service.delete(id);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'delete restaurant' in {} ms", stopWatch.getTotalTimeMillis());

        return ResponseEntity.noContent().build();
    }
}

