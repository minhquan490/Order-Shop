package com.bachlinh.order.web.handler.rest.common;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.service.business.ForgotPasswordService;
import org.springframework.http.HttpStatus;

import java.util.Map;

@RouteProvider(name = "forgotPasswordSendMailHandler")
@ActiveReflection
public class ForgotPasswordSendMailHandler extends AbstractController<NativeResponse<?>, Map<String, Object>> {
    private String url;
    private ForgotPasswordService forgotPasswordService;

    private ForgotPasswordSendMailHandler() {
        super();
    }

    @Override
    public AbstractController<NativeResponse<?>, Map<String, Object>> newInstance() {
        return new ForgotPasswordSendMailHandler();
    }

    @Override
    @ActiveReflection
    protected NativeResponse<?> internalHandler(Payload<Map<String, Object>> request) {
        String email = (String) request.data().get("email");
        forgotPasswordService.sendEmailResetPassword(email);
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
            url = getEnvironment().getProperty("shop.url.customer.reset.sending-mail");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
