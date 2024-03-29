package com.bachlinh.order.web.handler.rest.common;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.exception.http.ResourceNotFoundException;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.service.business.ForgotPasswordService;
import org.springframework.http.HttpStatus;

import java.util.Map;

@RouteProvider(name = "resetPasswordHandler")
@ActiveReflection
public class ResetPasswordHandler extends AbstractController<NativeResponse<?>, Map<String, Object>> {
    private String url;
    private ForgotPasswordService forgotPasswordService;

    private ResetPasswordHandler() {
        super();
    }

    @Override
    public AbstractController<NativeResponse<?>, Map<String, Object>> newInstance() {
        return new ResetPasswordHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected NativeResponse<?> internalHandler(Payload<Map<String, Object>> request) {
        var token = (String) request.data().get("token");
        if (token == null || token.isEmpty()) {
            throw new ResourceNotFoundException("Your request url not existed", url);
        }
        String newPassword = (String) request.data().get("password");
        if (newPassword == null || newPassword.isBlank()) {
            throw new ResourceNotFoundException("Url not existed", url);
        }
        forgotPasswordService.resetPassword(token, newPassword);
        NativeResponse<?> response = getNativeResponse();
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    @Override
    protected void inject() {
        if (forgotPasswordService == null) {
            forgotPasswordService = resolveService(ForgotPasswordService.class);
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
