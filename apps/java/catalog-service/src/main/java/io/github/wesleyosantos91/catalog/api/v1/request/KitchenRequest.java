package io.github.wesleyosantos91.catalog.api.v1.request;

import jakarta.validation.constraints.NotBlank;

public record KitchenRequest(@NotBlank String name) {}
