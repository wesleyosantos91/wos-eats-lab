package io.github.wesleyosantos91.catalog.api.v1.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RestaurantResponse(
        UUID id,
        String name,
        KitchenResponse kitchen,
        Boolean active,
        BigDecimal deliveryFee
) {}
