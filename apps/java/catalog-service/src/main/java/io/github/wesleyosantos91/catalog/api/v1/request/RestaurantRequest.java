package io.github.wesleyosantos91.catalog.api.v1.request;

import io.github.wesleyosantos91.catalog.core.validation.Groups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record RestaurantRequest(
        @NotBlank(groups = Groups.Create.class)
        @Size(max = 120, groups = {Groups.Create.class, Groups.Update.class})
        String name,

        @NotNull(groups = Groups.Create.class)
        UUID kitchenId,

        Boolean active,

        @NotNull(groups = Groups.Create.class)
        @Positive(groups = {Groups.Create.class, Groups.Update.class})
        BigDecimal deliveryFee
) {}
