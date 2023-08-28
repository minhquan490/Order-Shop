package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.TemporaryToken;

public interface TemporaryTokenRepository extends NativeQueryRepository {
    void saveTemporaryToken(TemporaryToken token);

    TemporaryToken getTemporaryToken(String tokenValue);
}
