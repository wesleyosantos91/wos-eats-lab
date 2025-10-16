package io.github.wesleyosantos91.catalog.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Modelo unificado para operações com Restaurant (Create, Update e Query).
 * Simplifica a manutenção ao ter um único modelo ao invés de 3 separados.
 *
 * <p>Para operações de Query, os campos minDeliveryFee e maxDeliveryFee podem ser usados para filtros.
 * Para operações de Create/Update, usa-se apenas o campo deliveryFee.
 * Os campos id e kitchen são populados apenas em operações de leitura (findById, search).
 * Para Create/Update, pode-se usar kitchenId (construtor simplificado) ou kitchen com apenas o ID.
 */
public record RestaurantModel(
        UUID id,
        String name,
        KitchenModel kitchen,
        Boolean active,
        BigDecimal deliveryFee,
        BigDecimal minDeliveryFee,
        BigDecimal maxDeliveryFee
) implements Serializable {
    /**
     * Construtor simplificado para operações de Create/Update (sem id, kitchen completo e filtros de range).
     * Cria um KitchenModel apenas com o ID.
     */
    public RestaurantModel(String name, UUID kitchenId, Boolean active, BigDecimal deliveryFee) {
        this(null, name, new KitchenModel(kitchenId, null), active, deliveryFee, null, null);
    }
    
    /**
     * Retorna o ID da cozinha, útil para operações que precisam apenas do ID.
     */
    public UUID kitchenId() {
        return kitchen != null ? kitchen.id() : null;
    }
    
    /**
     * Retorna o nome da cozinha, útil para responses.
     */
    public String kitchenName() {
        return kitchen != null ? kitchen.name() : null;
    }
}
