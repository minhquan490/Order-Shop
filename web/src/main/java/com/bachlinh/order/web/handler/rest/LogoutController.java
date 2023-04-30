package com.bachlinh.order.web.handler.rest;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeCookie;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.service.business.LogoutService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ActiveReflection
public class LogoutController extends AbstractController<NativeResponse<Map<String, Object>>, Object> {
    private static final String STATUS = "status";

    private String url;
    private LogoutService logoutService;
    private String clientCookieKey;
    private String clientCookieDomain;

    @ActiveReflection
    public LogoutController() {

    }

    @Override
    protected NativeResponse<Map<String, Object>> internalHandler(Payload<Object> request) {
        return logout();
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        Environment environment = getEnvironment();
        if (logoutService == null) {
            logoutService = resolver.resolveDependencies(LogoutService.class);
        }
        if (clientCookieKey == null) {
            clientCookieKey = environment.getProperty("server.cookie.name");
        }
        if (clientCookieDomain == null) {
            clientCookieDomain = environment.getProperty("server.cookie.domain");
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
        Map<String, NativeCookie> cookies = Arrays.stream(getNativeRequest().getCookies()).collect(Collectors.toMap(NativeCookie::name, nativeCookie -> nativeCookie));
        boolean result = logoutService.logout((Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), cookies.get(clientCookieKey).value());
        Map<String, Object> resp = new HashMap<>(2);
        NativeResponse.NativeResponseBuilder<Map<String, Object>> builder = NativeResponse.builder();
        if (result) {
            cookies.put(clientCookieKey, new NativeCookie("/", 0, true, true, "", clientCookieKey, clientCookieDomain));
            resp.put("message", "Logout success");
            resp.put(STATUS, HttpStatus.OK.value());
            builder.cookies(cookies.values().toArray(new NativeCookie[0]));
        } else {
            resp.put("message", "Logout failure, please contact for admin");
            resp.put(STATUS, HttpStatus.SERVICE_UNAVAILABLE.value());
        }
        return builder.body(resp).statusCode((Integer) resp.get(STATUS)).build();
    }
}
