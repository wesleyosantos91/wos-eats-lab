package io.github.wesleyosantos91.catalog.api.v1.request;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.web.bind.annotation.BindParam;


public record RestaurantQueryRequest(
        String name,
        @BindParam("kitchen_id")
        UUID kitchenId,
        Boolean active,
        @BindParam("min_delivery_fee")
        BigDecimal minDeliveryFee,
        @BindParam("max_delivery_fee")
        BigDecimal maxDeliveryFee
) {}
