package io.github.wesleyosantos91.catalog.domain.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Modelo unificado para operações com Kitchen (Create, Update e Query).
 * Simplifica a manutenção ao ter um único modelo ao invés de 3 separados.
 * O campo id é populado apenas em operações de leitura (findById, search).
 */
public record KitchenModel(UUID id, String name) implements Serializable {
    /**
     * Construtor simplificado para operações de Create/Update (sem id).
     */
    public KitchenModel(String name) {
        this(null, name);
    }
}
