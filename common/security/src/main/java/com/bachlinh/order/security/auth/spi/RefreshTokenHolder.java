package com.bachlinh.order.security.auth.spi;

import com.bachlinh.order.entity.model.RefreshToken;

import java.util.Optional;

public class RefreshTokenHolder {
    private final Optional<RefreshToken> token;

    public RefreshTokenHolder(RefreshToken refreshToken) {
        this.token = Optional.ofNullable(refreshToken);
    }

    public boolean isNonNull() {
        return token.isPresent();
    }

    public RefreshToken getValue() {
        if (!isNonNull()) {
            throw new IllegalStateException("Can not access to null token");
        }
        return token.get();
    }
}
