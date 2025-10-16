package io.github.wesleyosantos91.catalog.domain.repository;

import io.github.wesleyosantos91.catalog.domain.entity.ProductEntity;
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
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    boolean existsByNameAndRestaurantId(String name, UUID restaurantId);

    @Query("SELECT p FROM ProductEntity p JOIN FETCH p.restaurant WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithRestaurant(@Param("id") UUID id);

    @Query("""
            SELECT p 
            FROM ProductEntity p 
            WHERE 
                (:restaurantId IS NULL OR p.restaurant.id = :restaurantId) 
                AND (:name IS NULL OR LOWER(CAST(p.name AS string)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))) 
                AND (:minPrice IS NULL OR p.price >= :minPrice) 
                AND (:maxPrice IS NULL OR p.price <= :maxPrice) 
                AND (:active IS NULL OR p.active = :active)
            """)
    Page<ProductEntity> findByFilters(@Param("restaurantId") UUID restaurantId,
                                     @Param("name") String name,
                                     @Param("minPrice") BigDecimal minPrice,
                                     @Param("maxPrice") BigDecimal maxPrice,
                                     @Param("active") Boolean active,
                                     Pageable pageable);
}
