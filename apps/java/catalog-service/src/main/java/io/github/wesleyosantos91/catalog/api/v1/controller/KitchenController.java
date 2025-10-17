package io.github.wesleyosantos91.catalog.api.v1.controller;

import static io.github.wesleyosantos91.catalog.core.mapper.KitchenMapper.MAPPER;

import io.github.wesleyosantos91.catalog.api.v1.request.KitchenQueryRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.KitchenRequest;
import io.github.wesleyosantos91.catalog.api.v1.response.KitchenResponse;
import io.github.wesleyosantos91.catalog.core.port.in.kitchen.KitchenServicePort;
import io.github.wesleyosantos91.catalog.core.validation.Groups;
import io.github.wesleyosantos91.catalog.domain.exception.ResourceNotFoundException;
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
@RequestMapping("/v1/kitchens")
public record KitchenController(KitchenServicePort service) {

    private static final Logger LOGGER = LoggerFactory.getLogger(KitchenController.class);

    @PostMapping
    public ResponseEntity<KitchenResponse> create(@Validated(Groups.Create.class) @RequestBody KitchenRequest request) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'create kitchen'");
        final var model = MAPPER.toModel(request);
        final var createdModel = service.create(model);
        final var response = MAPPER.toResponse(createdModel);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'create kitchen {}' in {} ms", response, stopWatch.getTotalTimeMillis());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<KitchenResponse> getById(@PathVariable UUID id) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'getById kitchen' with id {}", id);
        final var model = service.findById(id);
        final var response = MAPPER.toResponse(model);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'getById kitchen' {} in {} ms", response, stopWatch.getTotalTimeMillis());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<PagedModel<KitchenResponse>> search(@ModelAttribute KitchenQueryRequest query, Pageable page) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.info("Function started 'find kitchen'");
        final var queryModel = MAPPER.toQueryModel(query);
        final var pageModel = service.search(queryModel, page);
        stopWatch.stop();
        LOGGER.info("finished function with kitchen 'find person' in {} ms", stopWatch.getTotalTimeMillis());

        return ResponseEntity.ok().body(new PagedModel<>(MAPPER.toPageResponse(pageModel)));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<KitchenResponse> update(@PathVariable UUID id,
                                                  @Validated(Groups.Update.class)
                                                  @RequestBody KitchenRequest request) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'update kitchen'");
        final var model = MAPPER.toModel(request);
        final var updatedModel = service.update(id, model);
        final var response = MAPPER.toResponse(updatedModel);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'update kitchen' {} in {} ms", response, stopWatch.getTotalTimeMillis());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'delete kitchen' with id {}", id);
        service.delete(id);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'delete person' in {} ms", stopWatch.getTotalTimeMillis());

        return ResponseEntity.noContent().build();
    }
}
