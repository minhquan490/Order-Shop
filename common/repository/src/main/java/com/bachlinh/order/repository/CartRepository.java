package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Cart;

public interface CartRepository {

    Cart saveCart(Cart cart);

    Cart updateCart(Cart cart);

    void deleteCart(Cart cart);
}
