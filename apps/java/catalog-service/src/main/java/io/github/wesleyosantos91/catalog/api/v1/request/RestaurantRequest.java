package io.github.wesleyosantos91.catalog.api.v1.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record RestaurantRequest(
        @NotBlank
        @Size(max = 120)
        String name,

        @NotNull
        UUID kitchenId,

        Boolean active,

        @NotNull
        @Positive
        BigDecimal deliveryFee
) {}
