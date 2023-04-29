package com.bachlinh.order.security.helper;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.exception.http.UnAuthorizationException;
import com.bachlinh.order.security.auth.spi.RefreshTokenHolder;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.utils.HeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

public final class AuthenticationHelper {

    private AuthenticationHelper() {
    }

    public static Map<String, Object> parseAuthentication(HttpServletRequest request, HttpServletResponse response, TokenManager tokenManager) {
        Map<String, Object> claims = new HashMap<>();

        String jwtToken = HeaderUtils.getAuthorizeHeader(request);
        String refreshToken = HeaderUtils.getRefreshHeader(request);
        if (jwtToken == null && refreshToken == null) {
            return claims;
        }
        if (jwtToken != null && refreshToken == null) {
            claims.putAll(tokenManager.getClaimsFromToken(jwtToken));
        }
        if (jwtToken == null) {
            jwtToken = tryToRevoke(refreshToken, tokenManager);
            claims.putAll(tokenManager.getClaimsFromToken(jwtToken));
        }
        if (claims.isEmpty()) {
            jwtToken = tryToRevoke(refreshToken, tokenManager);
            claims.putAll(tokenManager.getClaimsFromToken(jwtToken));
        }
        response.addHeader(HeaderUtils.getAuthorizeHeader(), jwtToken);
        return claims;
    }

    private static String tryToRevoke(String refreshToken, TokenManager tokenManager) {
        RefreshTokenHolder holder = tokenManager.validateRefreshToken(refreshToken);
        String jwt;
        if (holder.isNonNull()) {
            Customer customer = holder.getValue().getCustomer();
            tokenManager.encode(Customer_.ID, customer.getId());
            tokenManager.encode(Customer_.USERNAME, customer.getUsername());
            jwt = tokenManager.getTokenValue();
        } else {
            throw new UnAuthorizationException("Token is expired");
        }
        return jwt;
    }
}
