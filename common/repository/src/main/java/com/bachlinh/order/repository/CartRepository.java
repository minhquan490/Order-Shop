package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.repository.NativeQueryRepository;

import java.util.Collection;

public interface CartRepository extends NativeQueryRepository {

    void saveCart(Cart cart);

    Cart getCartForUpdateCartDetail(Customer owner, Collection<String> productIds);

    Cart getCartForDeleteCartDetail(Customer owner, Collection<Integer> cartDetailIds);

    Cart getCartOfCustomer(String customerId);

    void deleteCart(Cart cart);
}
