package io.github.wesleyosantos91.catalog.core.mapper;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import io.github.wesleyosantos91.catalog.api.v1.request.RestaurantQueryRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.RestaurantRequest;
import io.github.wesleyosantos91.catalog.api.v1.response.RestaurantResponse;
import io.github.wesleyosantos91.catalog.infrastructure.database.entity.RestaurantEntity;
import io.github.wesleyosantos91.catalog.domain.model.RestaurantModel;
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
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {KitchenMapper.class}
)
public interface RestaurantMapper {

    RestaurantMapper MAPPER = Mappers.getMapper(RestaurantMapper.class);

    @Mapping(target = "kitchen.id", source = "request.kitchenId")
    RestaurantModel toModel(RestaurantRequest request);

    RestaurantModel toModel(RestaurantEntity entity);

    @Mapping(target = "kitchen.id", source = "request.kitchenId")
    RestaurantModel toQueryModel(RestaurantQueryRequest request);

    RestaurantEntity toEntity(RestaurantModel model);

    RestaurantEntity toEntity(RestaurantModel model, @MappingTarget RestaurantEntity entity);


    RestaurantResponse toResponse(RestaurantModel model);

    default List<RestaurantModel> toListDomain(List<RestaurantEntity> entities) {
        final List<RestaurantModel> list = new ArrayList<>();
        entities.forEach(e -> list.add(toModel(e)));
        return list;
    }

    default Page<RestaurantModel> toPageDomain(Page<RestaurantEntity> pages) {
        final List<RestaurantModel> list = toListDomain(pages.getContent());
        return new PageImpl<>(list, pages.getPageable(), pages.getTotalElements());
    }

    default List<RestaurantResponse> toListResponse(List<RestaurantModel> models) {
        final List<RestaurantResponse> list = new ArrayList<>();
        models.forEach(m -> list.add(toResponse(m)));
        return list;
    }

    default Page<RestaurantResponse> toPageResponse(Page<RestaurantModel> pages) {
        final List<RestaurantResponse> list = toListResponse(pages.getContent());
        return new PageImpl<>(list, pages.getPageable(), pages.getTotalElements());
    }
}
