package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.converter.spi.ResponseConverter;
import com.bachlinh.order.core.http.handler.RequestHandler;
import com.bachlinh.order.core.http.translator.internal.JsonExceptionTranslator;
import com.bachlinh.order.core.http.translator.spi.ExceptionTranslator;
import com.bachlinh.order.handler.controller.ControllerContextHolder;
import com.bachlinh.order.handler.controller.ControllerManager;
import com.bachlinh.order.core.container.DependenciesResolver;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;

public interface Node extends RequestHandler, NodeHandler, ControllerContextHolder, ExceptionTranslator<NativeResponse<byte[]>> {

    @NonNull
    ResponseConverter<HttpServletResponse> getResponseConverter();

    @NonNull
    String getName();

    static Node getInstance(DependenciesResolver resolver, String name, Node parent) {
        ControllerManager controllerManager = resolver == null ? null : resolver.resolveDependencies(ControllerManager.class);
        JsonExceptionTranslator exceptionTranslator = resolver == null ? null : resolver.resolveDependencies(JsonExceptionTranslator.class);
        return new DefaultNode(controllerManager, exceptionTranslator, name, parent);
    }
}
