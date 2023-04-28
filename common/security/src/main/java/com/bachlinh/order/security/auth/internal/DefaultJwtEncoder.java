package com.bachlinh.order.security.auth.internal;

import com.bachlinh.order.security.auth.spi.JwtEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

class DefaultJwtEncoder implements JwtEncoder {
    private static final int MINUS_EXPIRE = 60;
    private final org.springframework.security.oauth2.jwt.JwtEncoder encoder;
    private final ThreadLocal<Map<String, Object>> internalProp = ThreadLocal.withInitial(HashMap::new);
    private final String header;

    DefaultJwtEncoder(org.springframework.security.oauth2.jwt.JwtEncoder encoder, String header) {
        this.encoder = encoder;
        this.header = header;
    }

    @Override
    public void encode(String key, Object value) {
        Map<String, Object> props = internalProp.get();
        props.put(key, value);
        internalProp.remove();
        internalProp.set(props);
    }

    @Override
    public void encode(Map<String, Object> payload) {
        Map<String, Object> props = internalProp.get();
        props.putAll(payload);
        internalProp.remove();
        internalProp.set(props);
    }

    @Override
    public String getTokenValue() {
        Map<String, Object> copyProp = internalProp.get();
        internalProp.remove();
        return encoder.encode(JwtEncoderParameters.from(buildHeader(), buildClaimsSet(copyProp))).getTokenValue();
    }

    private Instant calculateJwtExpiredTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusMinutes(MINUS_EXPIRE).toInstant(ZoneOffset.UTC);
    }

    private JwtClaimsSet buildClaimsSet(Map<String, Object> payload) {
        return JwtClaimsSet.builder()
                .claims(p -> p.putAll(payload))
                .expiresAt(calculateJwtExpiredTime())
                .build();
    }

    private JwsHeader buildHeader() {
        return JwsHeader.with(MacAlgorithm.from(header)).build();
    }
}
