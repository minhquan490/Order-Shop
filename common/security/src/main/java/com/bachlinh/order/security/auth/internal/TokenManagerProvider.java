package com.bachlinh.order.security.auth.internal;

import com.bachlinh.order.security.auth.spi.TokenManager;
import org.springframework.context.ApplicationContext;

public class TokenManagerProvider {
    private final String algorithm;
    private final String secretKey;
    private final ApplicationContext context;

    public TokenManagerProvider(String algorithm, String secretKey, ApplicationContext context) {
        this.algorithm = algorithm;
        this.secretKey = secretKey;
        this.context = context;
    }

    public TokenManager getTokenManager() {
        return new DefaultTokenManager(algorithm, secretKey, context);
    }
}