package io.github.wesleyosantos91.catalog.domain.model;

import java.io.Serializable;
import java.util.UUID;

public record KitchenModel(UUID id, String name) implements Serializable {
    public KitchenModel(String name) {
        this(null, name);
    }
}
