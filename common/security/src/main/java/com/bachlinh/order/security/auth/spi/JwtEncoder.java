package com.bachlinh.order.security.auth.spi;

import java.util.Map;

/**
 * Encoder for encoded attribute into payload of jwt token.
 *
 * @author Hoang Minh Quan.
 */
public interface JwtEncoder {

    /**
     * Add attribute with key oldValue into jwt token.
     */
    void encode(String key, Object value);

    /**
     * Add payload into jwt token.
     */
    void encode(Map<String, Object> payload);

    /**
     * Encode attribute and return oldValue of it.
     *
     * @return oldValue of jwt as string.
     */
    String getTokenValue();
}
