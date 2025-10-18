package io.github.wesleyosantos91.catalog.api.v1.request;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.web.bind.annotation.BindParam;

public record ProductQueryRequest(
        @BindParam("restaurant_id")
        UUID restaurantId,
        String name,
        @BindParam("min_price")
        BigDecimal minPrice,
        @BindParam("max_price")
        BigDecimal maxPrice,
        Boolean active
) {}
