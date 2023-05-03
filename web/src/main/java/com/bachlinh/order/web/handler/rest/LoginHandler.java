package com.bachlinh.order.web.handler.rest;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeCookie;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.UnAuthorizationException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.LoginForm;
import com.bachlinh.order.web.dto.resp.LoginResp;
import com.bachlinh.order.web.service.business.LoginService;

@RouteProvider
@ActiveReflection
public class LoginHandler extends AbstractController<NativeResponse<LoginResp>, LoginForm> {
    private String clientCookieKey;
    private String cookieDomain;
    private String loginUrl;
    private TokenManager tokenManager;
    private LoginService loginService;

    @ActiveReflection
    public LoginHandler() {
    }

    @Override
    protected NativeResponse<LoginResp> internalHandler(Payload<LoginForm> request) {
        LoginResp resp = login(request.data());
        String clientSecret = tokenManager.generateClientSecret(resp.refreshToken());
        NativeResponse.NativeResponseBuilder<LoginResp> builder = NativeResponse.builder();
        if (resp.isLogged()) {
            NativeCookie cookie = new NativeCookie("/", Integer.MAX_VALUE, true, true, clientSecret, clientCookieKey, cookieDomain);
            return builder.body(resp)
                    .cookies(new NativeCookie[]{cookie})
                    .build();
        } else {
            throw new UnAuthorizationException("Login failure");
        }
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (clientCookieKey == null) {
            clientCookieKey = getEnvironment().getProperty("server.cookie.name");
        }
        if (cookieDomain == null) {
            cookieDomain = getEnvironment().getProperty("server.cookie.domain");
        }
        if (tokenManager == null) {
            tokenManager = resolver.resolveDependencies(TokenManager.class);
        }
        if (loginService == null) {
            loginService = resolver.resolveDependencies(LoginService.class);
        }
    }

    @Override
    public String getPath() {
        if (loginUrl == null) {
            loginUrl = getEnvironment().getProperty("shop.url.login");
        }
        return loginUrl;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }

    private LoginResp login(LoginForm loginForm) {
        LoginResp loginResp = loginService.login(loginForm, getNativeRequest());
        if (!loginResp.isLogged()) {
            throw new UnAuthorizationException("Login failure");
        }
        return loginResp;
    }
}
