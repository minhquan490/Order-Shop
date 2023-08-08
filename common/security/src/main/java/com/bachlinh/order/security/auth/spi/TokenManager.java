package com.bachlinh.order.security.auth.spi;

import java.util.Map;

/**
 * The token manager for manage jwt token.
 *
 * @author Hoang Minh Quan.
 */
public interface TokenManager extends JwtDecoder, JwtEncoder {

    /**
     * Check jwt token is expired.
     *
     * @param token jwt token for checking.
     * @return true, if token is expired.
     */
    boolean isJwtExpired(String token);

    /**
     * Parse the jwt token and obtain the claim from it.
     *
     * @param token jwt token to parsing.
     * @return payload format in Map.
     */
    Map<String, Object> getClaimsFromToken(String token);

    /**
     * Parse the jwt token and obtain the header from it.
     *
     * @param token jwt token to parsing.
     * @return header format in Map.
     */
    Map<String, Object> getHeadersFromToken(String token);

    /**
     * Return generator for generate the refresh token.
     *
     * @return refresh token generator for generate the refresh token.
     */
    RefreshTokenGenerator getRefreshTokenGenerator();

    /**
     * Validate the refresh token.
     *
     * @param token token for validate.
     * @return refresh token holder hold refresh token or none.
     */
    RefreshTokenHolder validateRefreshToken(String token);
}
