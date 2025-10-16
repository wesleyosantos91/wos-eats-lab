package io.github.wesleyosantos91.catalog.domain.repository;

import io.github.wesleyosantos91.catalog.domain.entity.RestaurantEntity;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, UUID> {

    boolean existsByName(String name);

    @Query("SELECT r FROM RestaurantEntity r JOIN FETCH r.kitchen WHERE r.id = :id")
    Optional<RestaurantEntity> findByIdWithKitchen(@Param("id") UUID id);

    @Query("""
            SELECT r 
            FROM RestaurantEntity r 
            WHERE 
                (:name IS NULL OR LOWER(CAST(r.name AS string)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%')))
                AND (:kitchenId IS NULL OR r.kitchen.id = :kitchenId) 
                AND (:active IS NULL OR r.active = :active) 
                AND (:minDeliveryFee IS NULL OR r.deliveryFee >= :minDeliveryFee) 
                AND (:maxDeliveryFee IS NULL OR r.deliveryFee <= :maxDeliveryFee)
            """)
    Page<RestaurantEntity> findByFilters(@Param("name") String name,
                                        @Param("kitchenId") UUID kitchenId,
                                        @Param("active") Boolean active,
                                        @Param("minDeliveryFee") BigDecimal minDeliveryFee,
                                        @Param("maxDeliveryFee") BigDecimal maxDeliveryFee,
                                        Pageable pageable);
}
