package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Product;

public interface ProductCategoryRepository {
    void deleteProductCategory(Product product);

    void deleteProductCategory(Category category);
}
