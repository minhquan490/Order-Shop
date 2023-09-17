package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.Product;

public interface ProductCartRepository {
    void deleteProductCart(Product product);

    void deleteProductCart(Cart cart);
}
