package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.CartDetail;

import java.util.Collection;

public interface CartDetailRepository extends NativeQueryRepository {
    void updateCartDetails(Collection<CartDetail> cartDetails);

    void deleteCartDetails(Collection<CartDetail> cartDetails);
}
