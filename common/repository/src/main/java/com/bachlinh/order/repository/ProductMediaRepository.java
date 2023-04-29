package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.ProductMedia;

public interface ProductMediaRepository {
    ProductMedia loadMedia(int id);

    void saveMedia(ProductMedia productMedia);
}
