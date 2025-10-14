package io.github.wesleyosantos91.catalog.domain.repository;

import io.github.wesleyosantos91.catalog.domain.entity.KitchenEntity;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KitchenRepository extends JpaRepository<KitchenEntity, UUID> {

    boolean existsByName(@NotBlank String name);

    @Query("""
            SELECT k FROM KitchenEntity k 
            WHERE (:name IS NULL OR LOWER(k.name) LIKE LOWER(CONCAT('%', :name, '%')))
            """)
    Page<KitchenEntity> findByFilters(@Param("name") String name,
                                      Pageable pageable);
}
