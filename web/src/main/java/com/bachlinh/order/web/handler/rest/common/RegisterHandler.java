package com.bachlinh.order.web.handler.rest.common;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.exception.http.BadVariableException;
import com.bachlinh.order.core.exception.http.ValidationFailureException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.customer.RegisterForm;
import com.bachlinh.order.web.dto.resp.RegisterResp;
import com.bachlinh.order.web.service.business.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RouteProvider(name = "registerHandler")
@ActiveReflection
public class RegisterHandler extends AbstractController<RegisterResp, RegisterForm> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private String url;
    private RegisterService registerService;

    private RegisterHandler() {
    }

    @Override
    public AbstractController<RegisterResp, RegisterForm> newInstance() {
        return new RegisterHandler();
    }

    @Override
    @ActiveReflection
    protected RegisterResp internalHandler(Payload<RegisterForm> request) {
        RegisterResp resp = registerService.register(request.data());
        if (resp.isError()) {
            if (resp.exception() instanceof ValidationFailureException failureException) {
                throw failureException;
            } else {
                log.debug(resp.message(), resp.exception());
                throw new BadVariableException(resp.message(), resp.exception(), getPath());
            }
        }
        return resp;
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.register");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }

    @Override
    protected void inject() {
        if (registerService == null) {
            registerService = resolveService(RegisterService.class);
        }
    }
}
