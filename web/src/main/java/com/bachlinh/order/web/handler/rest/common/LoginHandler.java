package com.bachlinh.order.web.handler.rest.common;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.exception.http.UnAuthorizationException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.web.dto.form.common.LoginForm;
import com.bachlinh.order.web.dto.resp.LoginResp;
import com.bachlinh.order.web.service.business.LoginService;

@RouteProvider(name = "loginHandler")
@ActiveReflection
public class LoginHandler extends AbstractController<NativeResponse<LoginResp>, LoginForm> {
    private String loginUrl;
    private TokenManager tokenManager;
    private LoginService loginService;

    private LoginHandler() {
    }

    @Override
    public AbstractController<NativeResponse<LoginResp>, LoginForm> newInstance() {
        return new LoginHandler();
    }

    @Override
    @ActiveReflection
    protected NativeResponse<LoginResp> internalHandler(Payload<LoginForm> request) {
        LoginResp resp = loginService.login(request.data(), getNativeRequest());
        NativeResponse.NativeResponseBuilder<LoginResp> builder = NativeResponse.builder();
        if (resp.isLogged()) {
            return builder.body(resp).build();
        } else {
            throw new UnAuthorizationException("Login failure", loginUrl);
        }
    }

    @Override
    protected void inject() {
        if (tokenManager == null) {
            tokenManager = resolveDependencies(TokenManager.class);
        }
        if (loginService == null) {
            loginService = resolveService(LoginService.class);
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
}
