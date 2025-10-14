package io.github.wesleyosantos91.catalog.api.v1.request;

import io.github.wesleyosantos91.catalog.core.validation.Groups;
import jakarta.validation.constraints.NotBlank;

public record KitchenRequest(
        @NotBlank(groups = {Groups.Create.class, Groups.Update.class})
        String name
) {}
