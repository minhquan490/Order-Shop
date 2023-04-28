package com.bachlinh.order.security.auth.spi;

public interface ClientSecretManager {
    String generateClientSecret(String refreshToken);

    boolean isWrapped(String clientSecret);

    void removeClientSecret(String clientSecret, String refreshToken);
}
