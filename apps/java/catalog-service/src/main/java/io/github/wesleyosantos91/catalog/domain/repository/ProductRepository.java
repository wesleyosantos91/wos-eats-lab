package io.github.wesleyosantos91.catalog.domain.repository;

import io.github.wesleyosantos91.catalog.domain.entity.ProductEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
}
