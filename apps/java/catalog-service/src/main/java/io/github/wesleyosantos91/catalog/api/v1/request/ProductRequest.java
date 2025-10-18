package io.github.wesleyosantos91.catalog.api.v1.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.github.wesleyosantos91.catalog.core.validation.Groups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.web.bind.annotation.BindParam;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ProductRequest(
        @BindParam("restaurant_id")
        @NotNull(groups = Groups.Create.class)
        UUID restaurantId,

        @NotBlank(groups = Groups.Create.class)
        @Size(max = 120, groups = {Groups.Create.class, Groups.Update.class})
        String name,

        @Size(max = 500, groups = {Groups.Create.class, Groups.Update.class})
        String description,

        @NotNull(groups = Groups.Create.class)
        @Positive(groups = {Groups.Create.class, Groups.Update.class})
        BigDecimal price,

        Boolean active
) {}
