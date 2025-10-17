package io.github.wesleyosantos91.catalog.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record RestaurantModel(
        UUID id,
        String name,
        KitchenModel kitchen,
        Boolean active,
        BigDecimal deliveryFee,
        BigDecimal minDeliveryFee,
        BigDecimal maxDeliveryFee
) implements Serializable {
    public RestaurantModel(String name, UUID kitchenId, Boolean active, BigDecimal deliveryFee) {
        this(null, name, new KitchenModel(kitchenId, null), active, deliveryFee, null, null);
    }

    public UUID kitchenId() {
        return kitchen != null ? kitchen.id() : null;
    }

    public String kitchenName() {
        return kitchen != null ? kitchen.name() : null;
    }
}
