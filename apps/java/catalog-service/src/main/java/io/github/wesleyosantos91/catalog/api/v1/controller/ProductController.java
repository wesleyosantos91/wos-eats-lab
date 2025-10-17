package io.github.wesleyosantos91.catalog.api.v1.controller;

import io.github.wesleyosantos91.catalog.api.v1.request.ProductQueryRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.ProductRequest;
import io.github.wesleyosantos91.catalog.api.v1.response.ProductResponse;
import io.github.wesleyosantos91.catalog.core.mapper.ProductMapper;
import io.github.wesleyosantos91.catalog.core.port.in.product.ProductServicePort;
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
@RequestMapping("/v1/products")
public record ProductController(ProductServicePort service) {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Validated(Groups.Create.class) @RequestBody ProductRequest request) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'create product'");
        final var model = ProductMapper.MAPPER.toModel(request);
        final var createdModel = service.create(model);
        final var response = ProductMapper.MAPPER.toResponse(createdModel);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'create product {}' in {} ms", response, stopWatch.getTotalTimeMillis());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'getById product' with id {}", id);
        final var model = service.findById(id);
        final var response = ProductMapper.MAPPER.toResponse(model);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'getById product' {} in {} ms", response, stopWatch.getTotalTimeMillis());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<PagedModel<ProductResponse>> search(@ModelAttribute ProductQueryRequest query, Pageable page) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.info("Function started 'find product'");
        final var queryModel = ProductMapper.MAPPER.toQueryModel(query);
        final var pageModel = service.search(queryModel, page);
        stopWatch.stop();
        LOGGER.info("finished function with product 'find product' in {} ms", stopWatch.getTotalTimeMillis());

        return ResponseEntity.ok().body(new PagedModel<>(ProductMapper.MAPPER.toPageResponse(pageModel)));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id,
                                                  @Validated(Groups.Update.class)
                                                  @RequestBody ProductRequest request) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'update product'");
        final var model = ProductMapper.MAPPER.toModel(request);
        final var updatedModel = service.update(id, model);
        final var response = ProductMapper.MAPPER.toResponse(updatedModel);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'update product' {} in {} ms", response, stopWatch.getTotalTimeMillis());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'delete product' with id {}", id);
        service.delete(id);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'delete product' in {} ms", stopWatch.getTotalTimeMillis());

        return ResponseEntity.noContent().build();
    }
}

