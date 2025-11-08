package io.github.wesleyosantos91.catalog.domain.port.in.restaurant;

import io.github.wesleyosantos91.catalog.domain.model.RestaurantModel;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantServicePort {

    RestaurantModel create(RestaurantModel model);

    RestaurantModel findById(UUID id);

    Page<RestaurantModel> search(RestaurantModel queryModel, Pageable pageable);

    RestaurantModel update(UUID id, RestaurantModel model);

    void delete(UUID id);
}
