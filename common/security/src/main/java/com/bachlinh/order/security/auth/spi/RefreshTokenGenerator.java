package com.bachlinh.order.security.auth.spi;

import com.bachlinh.order.core.entity.model.RefreshToken;

/**
 * Generator for generate refresh token with customer id and username.
 *
 * @author Hoang Minh Quan
 */
public interface RefreshTokenGenerator {

    /**
     * Generate the refresh token.
     *
     * @param customerId id of customer.
     * @param username   username of customer.
     * @return refresh token.
     */
    RefreshToken generateToken(Object customerId, String username);
}
