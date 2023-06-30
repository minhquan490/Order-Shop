package com.bachlinh.order.web.handler.rest.common;

import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.RegisterForm;
import com.bachlinh.order.web.dto.resp.RegisterResp;
import com.bachlinh.order.web.service.business.RegisterService;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
public class RegisterHandler extends AbstractController<RegisterResp, RegisterForm> {
    private String url;

    private RegisterService registerService;

    @Override
    @ActiveReflection
    protected RegisterResp internalHandler(Payload<RegisterForm> request) {
        RegisterResp resp = registerService.register(request.data());
        if (resp.isError()) {
            throw new BadVariableException(resp.message());
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
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (registerService == null) {
            registerService = resolver.resolveDependencies(RegisterService.class);
        }
    }
}
