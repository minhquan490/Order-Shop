package com.bachlinh.order.security.auth.internal;

import com.bachlinh.order.security.auth.spi.JwtDecoder;
import com.bachlinh.order.security.auth.spi.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

class JwtDecoderFactoryImpl implements JwtDecoderFactory {

    private final org.springframework.security.oauth2.jwt.JwtDecoder decoder;

    JwtDecoderFactoryImpl(String algorithm, String secretKey) {
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), algorithm);
        this.decoder = NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Override
    public JwtDecoder buildDecoder() {
        return new DefaultJwtDecoder(decoder);
    }
}
