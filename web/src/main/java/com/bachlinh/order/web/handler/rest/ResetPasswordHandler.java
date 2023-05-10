package com.bachlinh.order.web.handler.rest;

import org.springframework.http.HttpStatus;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.service.business.ForgotPasswordService;

import java.util.Map;

@RouteProvider
@ActiveReflection
public class ResetPasswordHandler extends AbstractController<NativeResponse<?>, Map<String, Object>> {
    private String url;
    private ForgotPasswordService forgotPasswordService;

    @ActiveReflection
    public ResetPasswordHandler() {
        // Do nothing
    }

    @Override
    protected NativeResponse<?> internalHandler(Payload<Map<String, Object>> request) {
        var tokens = getNativeRequest().getUrlQueryParam().get("token");
        if (tokens == null || tokens.isEmpty()) {
            throw new ResourceNotFoundException("Your request url not existed");
        }
        String newPassword = (String) request.data().get("password");
        if (newPassword == null || newPassword.isBlank()) {
            throw new ResourceNotFoundException("Url not existed");
        }
        forgotPasswordService.resetPassword(tokens.get(0), newPassword);
        NativeResponse<?> response = getNativeResponse();
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    @Override
    protected void inject() {
        if (forgotPasswordService == null) {
            forgotPasswordService = getContainerResolver().getDependenciesResolver().resolveDependencies(ForgotPasswordService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.customer.reset.password");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}