package io.github.wesleyosantos91.catalog.api.v1.request;

import java.math.BigDecimal;
import java.util.UUID;

public record RestaurantQueryRequest(
        String name,
        UUID kitchenId,
        Boolean active,
        BigDecimal minDeliveryFee,
        BigDecimal maxDeliveryFee
) {}
