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
        byte[] image,
        BigDecimal minPrice,
        BigDecimal maxPrice
) implements Serializable {

    public ProductModel(UUID restaurantId, String name, String description, BigDecimal price, Boolean active, String imageKey) {
        this(null, new RestaurantModel(restaurantId, null, null, null, null, null, null),
                name, description, price, active, imageKey, null, null, null);
    }

    public ProductModel(String imageKey, byte[] image) {
        this(null, null, null, null, null, null, imageKey, image, null, null);
    }
}
