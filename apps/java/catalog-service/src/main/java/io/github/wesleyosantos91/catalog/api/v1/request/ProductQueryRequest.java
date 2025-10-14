package io.github.wesleyosantos91.catalog.api.v1.request;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductQueryRequest(
        UUID restaurantId,
        String name,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean active
) {}
