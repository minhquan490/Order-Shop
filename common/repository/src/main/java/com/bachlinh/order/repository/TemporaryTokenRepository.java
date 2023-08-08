package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.TemporaryToken;

public interface TemporaryTokenRepository extends NativeQueryRepository {
    void saveTemporaryToken(TemporaryToken token);

    void deleteTemporaryToken(Integer tokenId);

    void updateTemporaryToken(TemporaryToken token);

    TemporaryToken getTemporaryTokenOfCustomer(Customer customer);

    TemporaryToken getTemporaryToken(String tokenValue);
}
