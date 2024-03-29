package com.bachlinh.order.web.handler.rest.common;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.service.business.LogoutService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

@ActiveReflection
@RouteProvider(name = "logoutHandler")
public class LogoutHandler extends AbstractController<NativeResponse<Map<String, Object>>, Object> {
    private static final String STATUS = "status";
    private String url;
    private LogoutService logoutService;

    private LogoutHandler() {
        super();
    }

    @Override
    public AbstractController<NativeResponse<Map<String, Object>>, Object> newInstance() {
        return new LogoutHandler();
    }

    @Override
    @ActiveReflection
    protected NativeResponse<Map<String, Object>> internalHandler(Payload<Object> request) {
        return logout();
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (logoutService == null) {
            logoutService = resolver.resolveDependencies(LogoutService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.logout");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }

    private NativeResponse<Map<String, Object>> logout() {
        boolean result = logoutService.logout((Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Map<String, Object> resp = HashMap.newHashMap(2);
        NativeResponse.NativeResponseBuilder<Map<String, Object>> builder = NativeResponse.builder();
        if (result) {
            resp.putAll(createDefaultResponse(HttpStatus.OK.value(), new String[]{"Logout success"}));
        } else {
            resp.putAll(createDefaultResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), new String[]{"Logout failure, please contact for admin"}));
        }
        return builder.body(resp).statusCode((Integer) resp.get(STATUS)).build();
    }
}
