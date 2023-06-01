package com.bachlinh.order.security.auth.spi;

import jakarta.servlet.http.HttpServletRequest;

public interface InMemoryCsrfTokenCaching {
    String loadToken(String clientSecret);

    void storeToken(String token, String clientSecret);

    void releaseToken(HttpServletRequest servletRequest);
}
