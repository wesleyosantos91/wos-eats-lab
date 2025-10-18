package io.github.wesleyosantos91.catalog.api.v1.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.github.wesleyosantos91.catalog.core.validation.Groups;
import jakarta.validation.constraints.NotBlank;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KitchenRequest(
        @NotBlank(groups = {Groups.Create.class, Groups.Update.class})
        String name
) {}
