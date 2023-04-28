package com.bachlinh.order.security.auth.internal;

import com.bachlinh.order.security.auth.spi.JwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

class DefaultJwtDecoder implements JwtDecoder {
    private final org.springframework.security.oauth2.jwt.JwtDecoder decoder;

    DefaultJwtDecoder(org.springframework.security.oauth2.jwt.JwtDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        return decoder.decode(token);
    }
}
