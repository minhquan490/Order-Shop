package com.bachlinh.order.security.helper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.utils.HeaderUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AuthenticationHelper {
    private static final Map<String, HttpServletResponse> responseHolder = new ConcurrentHashMap<>();

    private AuthenticationHelper() {
    }

    public static void holdResponse(String requestId, HttpServletResponse response) {
        responseHolder.put(requestId, response);
    }

    public static HttpServletResponse getResponse(String requestId) {
        return responseHolder.get(requestId);
    }

    public static void release(String requestId) {
        responseHolder.remove(requestId);
    }

    public static String findClientSecret(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(HeaderUtils.getClientSecret())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static Map<String, Object> parseAuthentication(HttpServletRequest request, HttpServletResponse response, TokenManager tokenManager) {
        Map<String, Object> claims;
        String jwtToken = HeaderUtils.getAuthorizeHeader(request);
        claims = parseAuthentication(jwtToken, tokenManager);
        response.addHeader(HeaderUtils.getAuthorizeHeader(), jwtToken);
        return claims;
    }

    public static Map<String, Object> parseAuthentication(String jwtToken, TokenManager tokenManager) {
        Map<String, Object> claims = new HashMap<>();
        if (jwtToken == null) {
            return claims;
        } else {
            claims.putAll(tokenManager.getClaimsFromToken(jwtToken));
        }
        return claims;
    }
}
