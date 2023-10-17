package com.bachlinh.order.web.common.security;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.utils.DateTimeUtils;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.RefreshToken;
import com.bachlinh.order.repository.RepositoryManager;
import com.bachlinh.order.security.auth.internal.JwtFactoryBuilderProvider;
import com.bachlinh.order.security.auth.spi.JwtDecoder;
import com.bachlinh.order.security.auth.spi.JwtEncoder;
import com.bachlinh.order.security.auth.spi.RefreshTokenGenerator;
import com.bachlinh.order.security.auth.spi.RefreshTokenHolder;
import com.bachlinh.order.security.auth.spi.TemporaryTokenGenerator;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.web.repository.spi.CustomerRepository;
import com.bachlinh.order.web.repository.spi.RefreshTokenRepository;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.util.Assert;

class DefaultTokenManager implements TokenManager, RefreshTokenGenerator, TemporaryTokenGenerator {
    private static final String TEMP_TOKEN_ALGORITHM = "SHA-512";
    private static final String TEMP_TOKEN_KEY = "Refresh-account";

    private final JwtDecoder decoder;
    private final JwtEncoder encoder;
    private final EntityFactory entityFactory;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomerRepository customerRepository;

    DefaultTokenManager(String algorithm, String secretKey, DependenciesResolver resolver) {
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
        this.entityFactory = resolver.resolveDependencies(EntityFactory.class);
        RepositoryManager repositoryManager = resolver.resolveDependencies(RepositoryManager.class);
        this.refreshTokenRepository = repositoryManager.getRepository(RefreshTokenRepository.class);
        this.customerRepository = repositoryManager.getRepository(CustomerRepository.class);
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
            if (!isJwtExpired(token)) {
                return getJwtDecoder().decode(token).getClaims();
            }
        } catch (JwtException e) {
            // Jwt is expired, etc...
        }
        return HashMap.newHashMap(0);
    }

    @Override
    public Map<String, Object> getHeadersFromToken(String token) {
        try {
            return getJwtDecoder().decode(token).getHeaders();
        } catch (JwtException e) {
            return HashMap.newHashMap(0);
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
    public String revokeAccessToken(String refreshToken) {
        RefreshTokenHolder holder = validateRefreshToken(refreshToken);
        RefreshToken token = holder.getValue();
        if (token == null) {
            return "";
        } else {
            encode(Customer_.ID, token.getCustomer().getId());
            encode(Customer_.USERNAME, token.getCustomer().getUsername());
            return getTokenValue();
        }
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
        Customer customer = customerRepository.getCustomerForRefreshTokenGeneration((String) customerId);
        RefreshToken refreshToken = entityFactory.getEntity(RefreshToken.class);
        refreshToken.setCustomer(customer);
        Instant timeCreated = Instant.now();
        refreshToken.setTimeCreated(Timestamp.from(timeCreated));
        refreshToken.setTimeExpired(DateTimeUtils.calculateTimeRefreshTokenExpired(timeCreated));
        refreshToken.setRefreshTokenValue(UUID.randomUUID().toString());
        return refreshToken;
    }

    @Override
    public String generateTempToken() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(TEMP_TOKEN_ALGORITHM);
            byte[] encodedMessage = messageDigest.digest(TEMP_TOKEN_KEY.getBytes(StandardCharsets.UTF_8));
            BigInteger signum = new BigInteger(1, encodedMessage);
            StringBuilder token = new StringBuilder(signum.toString());
            while (token.length() < 32) {
                token.append("0");
                token.append(token);
            }
            return token.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CriticalException("Can not get encryptor with algorithm [" + TEMP_TOKEN_ALGORITHM + "]");
        }
    }
}
