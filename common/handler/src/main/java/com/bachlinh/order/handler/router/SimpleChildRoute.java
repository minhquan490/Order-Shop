package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeCookie;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.converter.spi.ResponseConverter;
import com.bachlinh.order.core.http.converter.spi.ServletCookieConverter;
import com.bachlinh.order.core.http.handler.SpringServletHandler;
import com.bachlinh.order.entity.transaction.spi.EntityTransactionManager;
import com.bachlinh.order.exception.http.HttpRequestMethodNotSupportedException;
import com.bachlinh.order.utils.map.LinkedMultiValueMap;
import com.bachlinh.order.utils.map.MultiValueMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SimpleChildRoute extends AbstractChildRoute implements SpringServletHandler {
    private static final Map<Class<?>, ResponseConverter<?>> converters = new ConcurrentHashMap<>();

    private final String path;
    private final ChildRoute parent;
    private final String rootPath;
    private final ServletCookieConverter<NativeCookie> nativeCookieConverter = ServletCookieConverter.servletCookieConverter();

    public SimpleChildRoute(@Nullable ChildRoute parent, String path, @Nullable String rootPath) {
        this.path = path;
        this.parent = parent;
        this.rootPath = rootPath;
    }

    @Override
    public ChildRoute getParent() {
        return parent;
    }

    @Override
    public String getRootPath() {
        return rootPath;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public SpringServletHandler getServletHandler() {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ResponseEntity<T> handleServletRequest(String controllerPath, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        try {
            setNativeRequest(NativeRequest.buildNativeFromServletRequest(servletRequest));
            setNativeResponse(parseFrom(servletResponse));
            RequestMethod method = RequestMethod.valueOf(servletRequest.getMethod().toUpperCase());
            NativeResponse<?> nativeResponse = handleRequest(getNativeRequest(), controllerPath, method);
            if (nativeResponse.getCookies() != null) {
                for (NativeCookie cookie : nativeResponse.getCookies()) {
                    servletResponse.addCookie(nativeCookieConverter.convert(cookie));
                }
            }
            MultiValueMap<String, String> headers = nativeResponse.getHeaders();
            if (headers == null) {
                headers = new LinkedMultiValueMap<>(0);
            }
            headers.forEach((key, values) -> values.forEach(value -> servletResponse.setHeader(key, value)));
            ResponseEntity.BodyBuilder builder = ResponseEntity.status(nativeResponse.getStatusCode());
            if (nativeResponse.getBody() != null) {
                return (ResponseEntity<T>) builder.body(nativeResponse.getBody());
            } else {
                return builder.build();
            }
        } catch (Throwable e) {
            NativeResponse<String> errorResponse;
            if (e instanceof Error error) {
                errorResponse = getExceptionTranslator().translateError(error);
            } else {
                errorResponse = getExceptionTranslator().translateException((Exception) e);
            }
            MultiValueMap<String, String> headers = errorResponse.getHeaders();
            if (headers == null) {
                headers = new LinkedMultiValueMap<>(0);
            }
            headers.forEach((key, values) -> values.forEach(value -> servletResponse.setHeader(key, value)));
            return (ResponseEntity<T>) ResponseEntity
                    .status(errorResponse.getStatusCode())
                    .body(errorResponse.getBody());
        }
    }

    @Override
    public <T, U> NativeResponse<T> handleRequest(NativeRequest<U> request, String controllerPath, RequestMethod method) throws HttpRequestMethodNotSupportedException {
        String controllerUrl = parseChild(controllerPath);
        NativeResponse<T> response = getControllerManager().handleRequest(request, controllerUrl, method);
        EntityTransactionManager transactionManager = getEntityFactory().getTransactionManager();
        if (transactionManager.hasSavePoint()) {
            transactionManager.getSavePointManager().release();
        }
        return response;
    }

    private String parseChild(String path) {
        String root = getRootPath();
        if (root == null) {
            ChildRoute childRoute = getParent();
            return parseChild(childRoute.getPath().concat(path));
        } else {
            if (root.equals(path)) {
                return path;
            }
            return root.concat(path);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> NativeResponse<T> parseFrom(HttpServletResponse response) {
        if (!converters.containsKey(HttpServletResponse.class)) {
            converters.put(HttpServletResponse.class, ResponseConverter.servletResponseConverter());
        }
        ResponseConverter<HttpServletResponse> converter = (ResponseConverter<HttpServletResponse>) converters.get(HttpServletResponse.class);
        return (NativeResponse<T>) converter.convert(response);
    }
}
