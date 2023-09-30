package com.bachlinh.order.web.handler.rest.common;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.exception.http.UnAuthorizationException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.core.utils.HeaderUtils;
import com.bachlinh.order.web.dto.resp.RevokeTokenResp;
import com.bachlinh.order.web.service.business.RevokeAccessTokenService;

@RouteProvider(name = "revokeAccessTokenHandler")
@ActiveReflection
public class RevokeAccessTokenHandler extends AbstractController<RevokeTokenResp, Void> {
    private String url;
    private RevokeAccessTokenService revokeAccessTokenService;

    private RevokeAccessTokenHandler() {
    }

    @Override
    public AbstractController<RevokeTokenResp, Void> newInstance() {
        return new RevokeAccessTokenHandler();
    }

    @Override
    @ActiveReflection
    protected RevokeTokenResp internalHandler(Payload<Void> request) {
        var refreshToken = getNativeRequest().getHeaders().getFirst(HeaderUtils.getRefreshHeader());
        if (refreshToken == null) {
            throw new UnAuthorizationException("Can not revoke access token", getPath());
        }
        return revokeAccessTokenService.revokeToken(refreshToken);
    }

    @Override
    protected void inject() {
        if (revokeAccessTokenService == null) {
            revokeAccessTokenService = resolveService(RevokeAccessTokenService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.revoke-token");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
