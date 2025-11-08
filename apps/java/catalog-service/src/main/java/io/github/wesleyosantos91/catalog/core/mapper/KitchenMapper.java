package io.github.wesleyosantos91.catalog.core.mapper;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import io.github.wesleyosantos91.catalog.api.v1.request.KitchenQueryRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.KitchenRequest;
import io.github.wesleyosantos91.catalog.api.v1.response.KitchenResponse;
import io.github.wesleyosantos91.catalog.infrastructure.database.entity.KitchenEntity;
import io.github.wesleyosantos91.catalog.domain.model.KitchenModel;
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

    KitchenModel toModel(KitchenRequest request);

    KitchenModel toModel(KitchenEntity entity);

    KitchenModel toQueryModel(KitchenQueryRequest request);

    KitchenEntity toEntity(KitchenModel model);

    KitchenEntity toEntity(KitchenModel model, @MappingTarget KitchenEntity entity);

    KitchenResponse toResponse(KitchenModel model);

    default List<KitchenModel> toListDomain(List<KitchenEntity> entities) {
        final List<KitchenModel> list = new ArrayList<>();
        entities.forEach(m -> list.add(toModel(m)));
        return list;
    }

    default Page<KitchenModel> toPageDomain(Page<KitchenEntity> pages) {
        final List<KitchenModel> list = toListDomain(pages.getContent());
        return new PageImpl<>(list, pages.getPageable(), pages.getTotalElements());

    }

    default List<KitchenResponse> toListResponse(List<KitchenModel> models) {
        final List<KitchenResponse> list = new ArrayList<>();
        models.forEach(m -> list.add(toResponse(m)));
        return list;
    }

    default Page<KitchenResponse> toPageResponse(Page<KitchenModel> pages) {
        final List<KitchenResponse> list = toListResponse(pages.getContent());
        return new PageImpl<>(list, pages.getPageable(), pages.getTotalElements());

    }
}
