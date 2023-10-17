package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.TemporaryToken;
import com.bachlinh.order.repository.NativeQueryRepository;

public interface TemporaryTokenRepository extends NativeQueryRepository {
    void saveTemporaryToken(TemporaryToken token);

    void deleteTemporaryToken(TemporaryToken token);

    TemporaryToken getTemporaryToken(String tokenValue);

    TemporaryToken getTemporaryToken(Customer owner);
}
