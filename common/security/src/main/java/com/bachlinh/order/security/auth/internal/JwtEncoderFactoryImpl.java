package com.bachlinh.order.security.auth.internal;

import com.bachlinh.order.security.auth.spi.JwtDecoderFactory;
import com.bachlinh.order.security.auth.spi.JwtEncoder;
import com.bachlinh.order.security.auth.spi.JwtEncoderFactory;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


class JwtEncoderFactoryImpl implements JwtEncoderFactory {
    private final org.springframework.security.oauth2.jwt.JwtEncoder encoder;
    private String header;

    JwtEncoderFactoryImpl(String algorithm, String secretKey) {
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), algorithm);
        JWKSource<SecurityContext> immutableSecret = new ImmutableSecret<>(key);
        this.encoder = new NimbusJwtEncoder(immutableSecret);
        if (algorithm.equals(JwtDecoderFactory.Builder.SHA256_ALGORITHM)) {
            this.header = JwsAlgorithms.HS256;
            return;
        }
        if (algorithm.equals(JwtDecoderFactory.Builder.SHA512_ALGORITHM)) {
            this.header = JwsAlgorithms.HS512;
        } else {
            throw new IllegalArgumentException("Does not support " + algorithm + " algorithm");
        }
    }

    @Override
    public JwtEncoder buildEncoder() {
        return new DefaultJwtEncoder(encoder, header);
    }
}
