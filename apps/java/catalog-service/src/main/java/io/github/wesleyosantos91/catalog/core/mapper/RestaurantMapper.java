package io.github.wesleyosantos91.catalog.core.mapper;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import io.github.wesleyosantos91.catalog.api.v1.request.RestaurantQueryRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.RestaurantRequest;
import io.github.wesleyosantos91.catalog.api.v1.response.RestaurantResponse;
import io.github.wesleyosantos91.catalog.domain.entity.KitchenEntity;
import io.github.wesleyosantos91.catalog.domain.entity.RestaurantEntity;
import java.util.ArrayList;
import java.util.List;
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
public interface RestaurantMapper {

    RestaurantMapper MAPPER = Mappers.getMapper(RestaurantMapper.class);

    RestaurantEntity toEntity(RestaurantQueryRequest query);

    @Mapping(target = "kitchen", source = "kitchenId")
    RestaurantEntity toEntity(RestaurantRequest request);

    @Mapping(target = "kitchen", source = "kitchenId")
    RestaurantEntity toEntity(RestaurantRequest request, @MappingTarget RestaurantEntity entity);

    @Mapping(target = "kitchenId", source = "kitchen.id")
    @Mapping(target = "kitchenName", source = "kitchen.name")
    RestaurantResponse toResponse(RestaurantEntity entity);

    default KitchenEntity mapKitchenId(java.util.UUID kitchenId) {
        if (kitchenId == null) {
            return null;
        }
        final KitchenEntity kitchen = new KitchenEntity();
        kitchen.setId(kitchenId);
        return kitchen;
    }

    default List<RestaurantResponse> toListResponse(List<RestaurantEntity> entities) {
        final List<RestaurantResponse> list = new ArrayList<>();
        entities.forEach(e -> list.add(toResponse(e)));
        return list;
    }

    default Page<RestaurantResponse> toPageResponse(Page<RestaurantEntity> pages) {
        final List<RestaurantResponse> list = toListResponse(pages.getContent());
        return new PageImpl<>(list, pages.getPageable(), pages.getTotalElements());
    }
}
