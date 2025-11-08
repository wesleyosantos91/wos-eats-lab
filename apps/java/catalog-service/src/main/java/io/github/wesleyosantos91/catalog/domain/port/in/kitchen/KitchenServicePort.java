package io.github.wesleyosantos91.catalog.domain.port.in.kitchen;

import io.github.wesleyosantos91.catalog.domain.model.KitchenModel;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface KitchenServicePort {

    KitchenModel create(KitchenModel model);

    KitchenModel findById(UUID id);

    Page<KitchenModel> search(KitchenModel queryModel, Pageable pageable);

    KitchenModel update(UUID id, KitchenModel model);

    void delete(UUID id);

}
