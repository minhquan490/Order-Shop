package com.bachlinh.order.security.auth.internal;

import com.bachlinh.order.security.auth.spi.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

@RequiredArgsConstructor
public class TokenManagerProvider {
    private final String algorithm;
    private final String secretKey;
    private final ApplicationContext context;

    public TokenManager getTokenManager() {
        return new DefaultTokenManager(algorithm, secretKey, context);
    }
}