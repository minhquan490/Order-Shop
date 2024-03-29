package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.ProductMedia;
import com.bachlinh.order.repository.NativeQueryRepository;

public interface ProductMediaRepository extends NativeQueryRepository {
    ProductMedia loadMedia(int id);

    void saveMedia(ProductMedia productMedia);

    void deleteMedia(String id);

    void deleteMedia(Product product);
}
