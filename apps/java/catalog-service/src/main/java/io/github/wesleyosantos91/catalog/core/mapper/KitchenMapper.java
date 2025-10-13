package io.github.wesleyosantos91.catalog.core.mapper;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import io.github.wesleyosantos91.catalog.api.v1.request.KitchenQueryRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.KitchenRequest;
import io.github.wesleyosantos91.catalog.api.v1.response.KitchenResponse;
import io.github.wesleyosantos91.catalog.domain.entity.KitchenEntity;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(nullValuePropertyMappingStrategy = IGNORE,
        nullValueCheckStrategy = ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KitchenMapper {

    KitchenMapper MAPPER = Mappers.getMapper(KitchenMapper.class);

    KitchenEntity toEntity(KitchenQueryRequest query);

    KitchenEntity toEntity(KitchenRequest request);

    KitchenEntity toEntity(KitchenRequest request, @MappingTarget KitchenEntity entity);

    KitchenResponse toResponse(KitchenEntity entity);

    default List<KitchenResponse> toListResponse(List<KitchenEntity> entities) {
        final List<KitchenResponse> list = new ArrayList<>();
        entities.forEach(e -> list.add(toResponse(e)));
        return list;
    }

    default Page<KitchenResponse> toPageResponse(Page<KitchenEntity> pages) {
        final List<KitchenResponse> list = toListResponse(pages.getContent());
        return new PageImpl<>(list, pages.getPageable(), pages.getTotalElements());

    }
}
