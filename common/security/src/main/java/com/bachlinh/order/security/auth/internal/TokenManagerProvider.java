package com.bachlinh.order.security.auth.internal;

import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.core.container.DependenciesContainerResolver;

public class TokenManagerProvider {
    private final String algorithm;
    private final String secretKey;
    private final ContainerWrapper containerWrapper;
    private final String profile;

    public TokenManagerProvider(ContainerWrapper containerWrapper, String profile) {
        Environment environment = Environment.getInstance(profile);
        this.algorithm = environment.getProperty("server.encode.algorithm");//JwtDecoderFactory#Builder#SHA256_ALGORITHM
        this.secretKey = environment.getProperty("server.secret.key");
        this.containerWrapper = containerWrapper;
        this.profile = profile;
    }

    public TokenManager getTokenManager() {
        DependenciesContainerResolver containerResolver = DependenciesContainerResolver.buildResolver(containerWrapper.unwrap(), profile);
        return new DefaultTokenManager(algorithm, secretKey, containerResolver.getDependenciesResolver());
    }
}