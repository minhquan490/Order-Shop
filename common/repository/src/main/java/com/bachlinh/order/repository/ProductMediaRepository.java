package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.ProductMedia;

public interface ProductMediaRepository extends NativeQueryRepository {
    ProductMedia loadMedia(int id);

    void saveMedia(ProductMedia productMedia);

    void deleteMedia(String id);
}
