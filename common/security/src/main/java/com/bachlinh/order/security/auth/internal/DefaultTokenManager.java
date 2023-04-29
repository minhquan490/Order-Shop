package com.bachlinh.order.security.auth.internal;

import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.RefreshToken;
import com.bachlinh.order.repository.RefreshTokenRepository;
import com.bachlinh.order.security.auth.spi.JwtDecoder;
import com.bachlinh.order.security.auth.spi.JwtEncoder;
import com.bachlinh.order.security.auth.spi.RefreshTokenGenerator;
import com.bachlinh.order.security.auth.spi.RefreshTokenHolder;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.handler.ClientSecretHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class DefaultTokenManager implements TokenManager, RefreshTokenGenerator {

    private final JwtDecoder decoder;
    private final JwtEncoder encoder;
    private final EntityFactory entityFactory;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ClientSecretHandler clientSecretHandler;

    DefaultTokenManager(String algorithm, String secretKey, ApplicationContext context) {
        this.decoder = JwtFactoryBuilderProvider.provideJwtDecoderFactoryBuilder()
                .algorithm(algorithm)
                .secretKey(secretKey)
                .build()
                .buildDecoder();
        this.encoder = JwtFactoryBuilderProvider.provideJwtEncoderFactoryBuilder()
                .algorithm(algorithm)
                .secretKey(secretKey)
                .build()
                .buildEncoder();
        this.entityFactory = context.getBean(EntityFactory.class);
        this.refreshTokenRepository = context.getBean(RefreshTokenRepository.class);
        this.clientSecretHandler = context.getBean(ClientSecretHandler.class);
    }

    JwtDecoder getJwtDecoder() {
        return decoder;
    }

    JwtEncoder getJwtEncoder() {
        return encoder;
    }

    @Override
    public boolean isJwtExpired(String token) {
        Instant expiredAt = getJwtDecoder().decode(token).getExpiresAt();
        Assert.notNull(expiredAt, "Time expired is null");
        Instant now = Instant.now();
        return now.compareTo(expiredAt) > 0;
    }

    @Override
    public Map<String, Object> getClaimsFromToken(String token) {
        try {
            return getJwtDecoder().decode(token).getClaims();
        } catch (JwtException e) {
            return new HashMap<>(1);
        }
    }

    @Override
    public Map<String, Object> getHeadersFromToken(String token) {
        try {
            return getJwtDecoder().decode(token).getHeaders();
        } catch (JwtException e) {
            return new HashMap<>(1);
        }
    }

    @Override
    public RefreshTokenGenerator getRefreshTokenGenerator() {
        return this;
    }

    @Override
    public RefreshTokenHolder validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.getRefreshToken(token);
        if (refreshToken == null) {
            return new RefreshTokenHolder(null);
        }
        if (refreshToken.getTimeExpired().compareTo(Timestamp.from(Instant.now())) > 0) {
            return new RefreshTokenHolder(refreshToken);
        }
        return new RefreshTokenHolder(null);
    }

    @Override
    public Jwt decode(String token) {
        return getJwtDecoder().decode(token);
    }

    @Override
    public void encode(String key, Object value) {
        getJwtEncoder().encode(key, value);
    }

    @Override
    public void encode(Map<String, Object> payload) {
        getJwtEncoder().encode(payload);
    }

    @Override
    public String getTokenValue() {
        return getJwtEncoder().getTokenValue();
    }

    @Override
    public RefreshToken generateToken(Object customerId, String username) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RefreshToken refreshToken = entityFactory.getEntity(RefreshToken.class);
        refreshToken.setCustomer(customer);
        refreshToken.setId(entityFactory.getEntityContext(RefreshToken.class).getNextId());
        Instant timeCreated = Instant.now();
        refreshToken.setTimeCreated(Timestamp.from(timeCreated));
        refreshToken.setTimeExpired(Timestamp.from(Instant.ofEpochSecond(timeCreated.getEpochSecond() + 86400 * 365)));
        refreshToken.setRefreshTokenValue(UUID.randomUUID().toString());
        return refreshToken;
    }

    @Override
    public String generateClientSecret(String refreshToken) {
        String clientSecret = UUID.randomUUID().toString();
        clientSecretHandler.setClientSecret(clientSecret, refreshToken);
        return clientSecret;
    }

    @Override
    public boolean isWrapped(String clientSecret) {
        return clientSecretHandler.getClientSecret(clientSecret) != null;
    }

    @Override
    public void removeClientSecret(String clientSecret, String refreshToken) {
        clientSecretHandler.removeClientSecret(refreshToken, clientSecret);
    }
}
