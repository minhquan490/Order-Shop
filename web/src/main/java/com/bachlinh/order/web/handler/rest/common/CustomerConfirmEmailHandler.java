package com.bachlinh.order.web.handler.rest.common;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.ConfirmEmailResp;
import com.bachlinh.order.web.service.business.ConfirmEmailService;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
@RouteProvider(name = "customerConfirmEmail")
public class CustomerConfirmEmailHandler extends AbstractController<ConfirmEmailResp, Void> {

    private String url;
    private ConfirmEmailService confirmEmailService;

    @Override
    @ActiveReflection
    protected ConfirmEmailResp internalHandler(Payload<Void> request) {
        String token = getNativeRequest().getUrlQueryParam().getFirst("token");
        if (!StringUtils.hasText(token)) {
            throw new ResourceNotFoundException("Not found !", getPath());
        }
        return confirmEmailService.confirmEmail(token);
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (confirmEmailService == null) {
            confirmEmailService = resolver.resolveDependencies(ConfirmEmailService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.confirm-email");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
