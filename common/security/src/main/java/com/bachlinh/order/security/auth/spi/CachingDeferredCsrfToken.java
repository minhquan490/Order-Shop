package com.bachlinh.order.security.auth.spi;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.DeferredCsrfToken;

public class CachingDeferredCsrfToken implements DeferredCsrfToken {
    private final String cookieAndHeaderName;
    private final String paramName;
    private final String token;

    public CachingDeferredCsrfToken(String cookieAndHeaderName, String paramName, String token) {
        this.cookieAndHeaderName = cookieAndHeaderName;
        this.paramName = paramName;
        this.token = token == null ? "" : token;
    }

    @Override
    public CsrfToken get() {
        return new DefaultCsrfToken(cookieAndHeaderName, paramName, token);
    }

    @Override
    public boolean isGenerated() {
        return false;
    }
}
