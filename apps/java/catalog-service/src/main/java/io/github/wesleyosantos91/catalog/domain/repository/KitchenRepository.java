package io.github.wesleyosantos91.catalog.domain.repository;

import io.github.wesleyosantos91.catalog.domain.entity.KitchenEntity;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitchenRepository extends JpaRepository<KitchenEntity, UUID> {

    boolean existsByName(@NotBlank String name);
}
