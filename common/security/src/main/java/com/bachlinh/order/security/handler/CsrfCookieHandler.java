package com.bachlinh.order.security.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.DeferredCsrfToken;
import com.bachlinh.order.security.auth.spi.CachingDeferredCsrfToken;
import com.bachlinh.order.security.auth.spi.CsrfTokenMatcher;
import com.bachlinh.order.security.auth.spi.InMemoryCsrfTokenCaching;
import com.bachlinh.order.security.auth.spi.TemporaryTokenGenerator;
import com.bachlinh.order.utils.HeaderUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CsrfCookieHandler implements CsrfTokenRepository, CsrfTokenMatcher, InMemoryCsrfTokenCaching {
    private static final String DEFAULT_HEADER_AND_COOKIE_NAME = "X-XSRF-TOKEN";
    private static final String PARAMETER_NAME = "_csrf";
    private final String path;
    private final String domain;
    private final TemporaryTokenGenerator tokenGenerator;
    private final Map<String, String> cachingCsrf = new ConcurrentHashMap<>();

    public CsrfCookieHandler(String path, String domain, TemporaryTokenGenerator tokenGenerator) {
        this.path = path;
        this.domain = domain;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        String token = generateToken();
        return new DefaultCsrfToken(DEFAULT_HEADER_AND_COOKIE_NAME, PARAMETER_NAME, token);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        String tokenValue = (token != null) ? token.getToken() : "";
        Cookie cookie = new Cookie(DEFAULT_HEADER_AND_COOKIE_NAME, tokenValue);
        cookie.setSecure(true);
        cookie.setMaxAge(-1);
        cookie.setPath(path);
        cookie.setHttpOnly(true);
        cookie.setDomain(domain);
        response.addCookie(cookie);
        String clientSecret = HeaderUtils.getRequestHeaderValue(HeaderUtils.getClientSecret(), request);
        if (!tokenValue.isBlank()) {
            storeToken(tokenValue, clientSecret);
        }
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(DEFAULT_HEADER_AND_COOKIE_NAME)) {
                return new DefaultCsrfToken(DEFAULT_HEADER_AND_COOKIE_NAME, PARAMETER_NAME, cookie.getValue());
            }
        }
        return null;
    }

    @Override
    public DeferredCsrfToken loadDeferredToken(HttpServletRequest request, HttpServletResponse response) {
        String clientSecret = HeaderUtils.getRequestHeaderValue(HeaderUtils.getClientSecret(), request);
        return new CachingDeferredCsrfToken(DEFAULT_HEADER_AND_COOKIE_NAME, PARAMETER_NAME, loadToken(clientSecret));
    }

    private String generateToken() {
        return tokenGenerator.generateTempToken();
    }

    @Override
    public boolean match(String actualToken, String expectedToken) {
        byte[] actualByte = actualToken.getBytes(StandardCharsets.UTF_8);
        byte[] expectedByte = expectedToken.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(actualByte, expectedByte);
    }

    @Override
    public String loadToken(String clientSecret) {
        return cachingCsrf.get(clientSecret);
    }

    @Override
    public void storeToken(String token, String clientSecret) {
        cachingCsrf.put(clientSecret, token);
    }

    @Override
    public void releaseToken(HttpServletRequest servletRequest) {
        String clientSecret = HeaderUtils.getRequestHeaderValue(HeaderUtils.getClientSecret(), servletRequest);
        cachingCsrf.remove(clientSecret);
    }
}
