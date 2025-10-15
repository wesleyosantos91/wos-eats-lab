package io.github.wesleyosantos91.catalog.cucumber.hooks;

import io.cucumber.java.Before;
import io.github.wesleyosantos91.catalog.domain.repository.KitchenRepository;
import io.github.wesleyosantos91.catalog.domain.repository.RestaurantRepository;

/**
 * Hooks do Cucumber para gerenciar a limpeza do banco de dados.
 * O @Before é executado antes de cada cenário para garantir estado inicial limpo.
 * Nota: Não usar @Component pois o Cucumber detecta automaticamente as classes glue.
 */
public class DatabaseCleanupHooks {

    private final KitchenRepository kitchenRepository;
    private final RestaurantRepository restaurantRepository;

    public DatabaseCleanupHooks(KitchenRepository kitchenRepository, RestaurantRepository restaurantRepository) {
        this.kitchenRepository = kitchenRepository;
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Limpa todas as tabelas antes de cada cenário Cucumber.
     * Garante que cada cenário inicie com banco de dados limpo.
     */
    @Before
    public void cleanupDatabase() {
        // Limpa na ordem correta respeitando as constraints de foreign key
        restaurantRepository.deleteAll();
        kitchenRepository.deleteAll();
    }
}
