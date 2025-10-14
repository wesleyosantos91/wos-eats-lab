package io.github.wesleyosantos91.catalog.domain.repository;

import io.github.wesleyosantos91.catalog.domain.entity.ProductEntity;
import java.math.BigDecimal;
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

    @Query("""
            SELECT p 
            FROM ProductEntity p 
            WHERE 
                (:restaurantId IS NULL OR p.restaurant.id = :restaurantId) 
                AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) 
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
