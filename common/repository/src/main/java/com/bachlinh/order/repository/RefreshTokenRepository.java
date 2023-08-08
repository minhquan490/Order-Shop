package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.RefreshToken;

public interface RefreshTokenRepository extends NativeQueryRepository {

    RefreshToken getRefreshToken(String token);

    RefreshToken getRefreshTokenByCustomer(Customer customer);

    RefreshToken saveRefreshToken(RefreshToken token);

    RefreshToken updateRefreshToken(RefreshToken refreshToken);

    boolean deleteRefreshToken(RefreshToken refreshToken);
}
