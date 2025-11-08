package io.github.wesleyosantos91.catalog.cucumber.hooks;

import io.cucumber.java.Before;
import io.github.wesleyosantos91.catalog.infrastructure.database.repository.KitchenRepository;
import io.github.wesleyosantos91.catalog.infrastructure.database.repository.RestaurantRepository;

public class DatabaseCleanupHooks {

    private final KitchenRepository kitchenRepository;
    private final RestaurantRepository restaurantRepository;

    public DatabaseCleanupHooks(KitchenRepository kitchenRepository, RestaurantRepository restaurantRepository) {
        this.kitchenRepository = kitchenRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Before
    public void cleanupDatabase() {
        restaurantRepository.deleteAll();
        kitchenRepository.deleteAll();
    }
}
