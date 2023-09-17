package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.entity.repository.NativeQueryRepository;

import java.util.Collection;

public interface CartDetailRepository extends NativeQueryRepository {
    void updateCartDetails(Collection<CartDetail> cartDetails);

    void deleteCartDetails(Collection<CartDetail> cartDetails);

    Collection<CartDetail> getCartDetailsOfCart(Cart cart);
}
