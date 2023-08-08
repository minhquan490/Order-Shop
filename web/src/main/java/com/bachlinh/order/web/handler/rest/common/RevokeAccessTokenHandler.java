package com.bachlinh.order.web.handler.rest.common;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.UnAuthorizationException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.utils.HeaderUtils;
import com.bachlinh.order.web.dto.resp.RevokeTokenResp;
import com.bachlinh.order.web.service.business.RevokeAccessTokenService;
import lombok.NoArgsConstructor;

@RouteProvider(name = "revokeAccessTokenHandler")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class RevokeAccessTokenHandler extends AbstractController<RevokeTokenResp, Void> {
    private String url;
    private RevokeAccessTokenService revokeAccessTokenService;

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
        var resolver = getContainerResolver().getDependenciesResolver();
        if (revokeAccessTokenService == null) {
            revokeAccessTokenService = resolver.resolveDependencies(RevokeAccessTokenService.class);
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
