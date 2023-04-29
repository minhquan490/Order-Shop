package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.RefreshToken;

public interface RefreshTokenRepository {

    RefreshToken getRefreshToken(String token);

    RefreshToken saveRefreshToken(RefreshToken token);

    RefreshToken updateRefreshToken(RefreshToken refreshToken);

    boolean deleteRefreshToken(RefreshToken refreshToken);
}
