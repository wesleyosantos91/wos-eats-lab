package io.github.wesleyosantos91.catalog.core.mapper;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import io.github.wesleyosantos91.catalog.api.v1.request.ProductQueryRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.ProductRequest;
import io.github.wesleyosantos91.catalog.api.v1.response.ProductResponse;
import io.github.wesleyosantos91.catalog.domain.entity.ProductEntity;
import io.github.wesleyosantos91.catalog.domain.entity.RestaurantEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(nullValuePropertyMappingStrategy = IGNORE,
        nullValueCheckStrategy = ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    ProductMapper MAPPER = Mappers.getMapper(ProductMapper.class);

    ProductEntity toEntity(ProductQueryRequest query);

    @Mapping(target = "restaurant", source = "restaurantId")
    ProductEntity toEntity(ProductRequest request);

    @Mapping(target = "restaurant", source = "restaurantId")
    ProductEntity toEntity(ProductRequest request, @MappingTarget ProductEntity entity);

    @Mapping(target = "restaurantId", source = "restaurant.id")
    ProductResponse toResponse(ProductEntity entity);

    default RestaurantEntity mapRestaurantId(UUID restaurantId) {
        if (restaurantId == null) {
            return null;
        }
        final RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);
        return restaurant;
    }

    default List<ProductResponse> toListResponse(List<ProductEntity> entities) {
        final List<ProductResponse> list = new ArrayList<>();
        entities.forEach(e -> list.add(toResponse(e)));
        return list;
    }

    default Page<ProductResponse> toPageResponse(Page<ProductEntity> pages) {
        final List<ProductResponse> list = toListResponse(pages.getContent());
        return new PageImpl<>(list, pages.getPageable(), pages.getTotalElements());
    }
}
