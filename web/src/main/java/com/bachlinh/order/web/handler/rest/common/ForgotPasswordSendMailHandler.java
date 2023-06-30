package com.bachlinh.order.web.handler.rest.common;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.service.business.ForgotPasswordService;

import java.util.Map;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
public class ForgotPasswordSendMailHandler extends AbstractController<NativeResponse<?>, Map<String, Object>> {
    private String url;
    private ForgotPasswordService forgotPasswordService;

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
            forgotPasswordService = getContainerResolver().getDependenciesResolver().resolveDependencies(ForgotPasswordService.class);
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
