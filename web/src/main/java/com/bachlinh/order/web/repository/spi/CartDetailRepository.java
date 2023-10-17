package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.repository.NativeQueryRepository;

import java.util.Collection;

public interface CartDetailRepository extends NativeQueryRepository {
    void updateCartDetails(Collection<CartDetail> cartDetails);

    void deleteCartDetails(Collection<CartDetail> cartDetails);

    Collection<CartDetail> getCartDetailsOfCart(Cart cart);
}
