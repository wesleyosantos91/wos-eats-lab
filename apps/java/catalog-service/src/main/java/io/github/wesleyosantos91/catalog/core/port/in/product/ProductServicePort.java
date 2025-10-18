package io.github.wesleyosantos91.catalog.core.port.in.product;

import io.github.wesleyosantos91.catalog.domain.model.ProductModel;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductServicePort {

    ProductModel create(ProductModel model, MultipartFile imageFile);

    ProductModel findById(UUID id);

    Page<ProductModel> search(ProductModel queryModel, Pageable pageable);

    ProductModel update(UUID id, ProductModel model, MultipartFile imageFile);

    void delete(UUID id);

    ProductModel deleteImage(UUID id);

    ProductModel getImage(UUID id);
}
