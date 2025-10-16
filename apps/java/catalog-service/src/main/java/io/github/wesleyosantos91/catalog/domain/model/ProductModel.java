package io.github.wesleyosantos91.catalog.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Modelo unificado para operações com Product (Create, Update e Query).
 * Simplifica a manutenção ao ter um único modelo ao invés de 3 separados.
 *
 * <p>Para operações de Query, os campos minPrice e maxPrice podem ser usados para filtros.
 * Para operações de Create/Update, usa-se os campos name, description, price, active e imageKey.
 * Os campos id e restaurant são populados apenas em operações de leitura (findById, search).
 * Para Create/Update, pode-se usar restaurantId (construtor simplificado) ou restaurant com apenas o ID.
 */
public record ProductModel(
        UUID id,
        RestaurantModel restaurant,
        String name,
        String description,
        BigDecimal price,
        Boolean active,
        String imageKey,
        BigDecimal minPrice,
        BigDecimal maxPrice
) implements Serializable {
    /**
     * Construtor simplificado para operações de Create/Update (sem id, restaurant completo e filtros de range).
     * Cria um RestaurantModel apenas com o ID (sem kitchen e demais campos).
     */
    public ProductModel(UUID restaurantId, String name, String description, BigDecimal price, Boolean active, String imageKey) {
        this(null, new RestaurantModel(restaurantId, null, null, null, null, null, null), name, description, price, active, imageKey, null, null);
    }
    
    /**
     * Retorna o ID do restaurante, útil para operações que precisam apenas do ID.
     */
    public UUID restaurantId() {
        return restaurant != null ? restaurant.id() : null;
    }
}
