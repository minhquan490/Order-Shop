package com.bachlinh.order.web.handler.websocket;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.exception.http.UnAuthorizationException;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.security.auth.spi.PrincipalHolder;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.handler.UnAuthorizationHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.RequestUpgradeStrategy;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public class ProxyRequestUpgradeStrategy implements RequestUpgradeStrategy {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String TOKEN_QUERY_PARAM = "token";
    private final JettyRequestUpgradeStrategy delegate = new JettyRequestUpgradeStrategy();
    private final CustomerRepository customerRepository;
    private final TokenManager tokenManager;
    private final UnAuthorizationHandler authorizationHandler;

    public ProxyRequestUpgradeStrategy(CustomerRepository customerRepository, TokenManager tokenManager, UnAuthorizationHandler authorizationHandler) {
        this.customerRepository = customerRepository;
        this.tokenManager = tokenManager;
        this.authorizationHandler = authorizationHandler;
    }

    @Override
    @NonNull
    public String[] getSupportedVersions() {
        return delegate.getSupportedVersions();
    }

    @Override
    @NonNull
    public List<WebSocketExtension> getSupportedExtensions(@NonNull ServerHttpRequest request) {
        return delegate.getSupportedExtensions(request);
    }

    @Override
    public void upgrade(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @Nullable String selectedProtocol, @NonNull List<WebSocketExtension> selectedExtensions, @Nullable Principal user, @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) throws HandshakeFailureException {
        Assert.isInstanceOf(ServletServerHttpRequest.class, request, "ServletServerHttpRequest required");
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        Assert.isInstanceOf(ServletServerHttpResponse.class, response, "ServletServerHttpResponse required");
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        var nativeRequest = NativeRequest.buildNativeFromServletRequest(servletRequest);
        var token = nativeRequest.getUrlQueryParam().getFirst(TOKEN_QUERY_PARAM);
        var claims = tokenManager.getClaimsFromToken(token);
        if (claims.isEmpty()) {
            try {
                authorizationHandler.onAuthenticationFailure(servletResponse, new UnAuthorizationException("Missing token, login is required", servletRequest.getRequestURI()));
            } catch (IOException e) {
                log.error("Can not establish socket connection", e);
                servletResponse.setStatus(500);
                return;
            }
        }
        var customer = customerRepository.getCustomerForAuthentication((String) claims.get("id"));
        var holder = new PrincipalHolder(customer, "");
        delegate.upgrade(request, response, selectedProtocol, selectedExtensions, holder, wsHandler, attributes);
    }
}
