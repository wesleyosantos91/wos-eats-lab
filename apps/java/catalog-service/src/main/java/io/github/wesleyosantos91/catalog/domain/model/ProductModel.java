package io.github.wesleyosantos91.catalog.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductModel(
        UUID id,
        RestaurantModel restaurant,
        String name,
        String description,
        BigDecimal price,
        Boolean active,
        String imageKey,
        BigDecimal minPrice,
        BigDecimal maxPrice
) implements Serializable {

    public ProductModel(UUID restaurantId, String name, String description, BigDecimal price, Boolean active, String imageKey) {
        this(null, new RestaurantModel(restaurantId, null, null, null, null, null, null), name, description, price, active, imageKey, null, null);
    }
}
