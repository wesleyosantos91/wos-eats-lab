package io.github.wesleyosantos91.catalog.core.mapper;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import io.github.wesleyosantos91.catalog.api.v1.request.ProductQueryRequest;
import io.github.wesleyosantos91.catalog.api.v1.request.ProductRequest;
import io.github.wesleyosantos91.catalog.api.v1.response.ProductResponse;
import io.github.wesleyosantos91.catalog.domain.entity.ProductEntity;
import io.github.wesleyosantos91.catalog.domain.model.ProductModel;
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
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {RestaurantMapper.class}
)
public interface ProductMapper {

    ProductMapper MAPPER = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "restaurant.id", source = "request.restaurantId")
    ProductModel toModel(ProductRequest request);

    ProductModel toModel(ProductEntity entity);

    @Mapping(target = "restaurant.id", source = "request.restaurantId")
    ProductModel toQueryModel(ProductQueryRequest request);

    ProductEntity toEntity(ProductModel model);

    ProductEntity toEntity(ProductModel model, @MappingTarget ProductEntity entity);

    @Mapping(target = "imagePath", expression = "java(toImagePath(model.id(), model.imageKey()))")
    ProductResponse toResponse(ProductModel model);

    default String toImagePath(UUID productId, String imageKey) {
        if (imageKey == null || imageKey.isBlank()) {
            return null;
        }
        return "/v1/products/" + productId + "/image";
    }

    default List<ProductModel> toListDomain(List<ProductEntity> entities) {
        final List<ProductModel> list = new ArrayList<>();
        entities.forEach(e -> list.add(toModel(e)));
        return list;
    }

    default Page<ProductModel> toPageDomain(Page<ProductEntity> pages) {
        final List<ProductModel> list = toListDomain(pages.getContent());
        return new PageImpl<>(list, pages.getPageable(), pages.getTotalElements());
    }

    default List<ProductResponse> toListResponse(List<ProductModel> models) {
        final List<ProductResponse> list = new ArrayList<>();
        models.forEach(m -> list.add(toResponse(m)));
        return list;
    }

    default Page<ProductResponse> toPageResponse(Page<ProductModel> pages) {
        final List<ProductResponse> list = toListResponse(pages.getContent());
        return new PageImpl<>(list, pages.getPageable(), pages.getTotalElements());
    }
}

