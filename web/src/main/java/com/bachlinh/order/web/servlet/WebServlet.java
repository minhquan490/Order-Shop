package com.bachlinh.order.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.core.server.grpc.GrpcHandlerAdapter;
import com.bachlinh.order.core.server.grpc.adapter.ServletResponseAdapter;
import com.bachlinh.order.security.helper.AuthenticationHelper;
import com.bachlinh.order.web.handler.SpringFrontRequestHandler;

@ActiveReflection
public class WebServlet extends GrpcHandlerAdapter {

    private SpringFrontRequestHandler frontRequestHandler;
    private final ApplicationContext applicationContext;

    @ActiveReflection
    @DependenciesInitialize
    public WebServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected ResponseEntity<?> processProtoRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        return frontRequestHandler.handle(servletRequest, servletResponse);
    }

    @Override
    protected ServletResponseAdapter getResponse(String requestId) {
        return (ServletResponseAdapter) AuthenticationHelper.getResponse(requestId);
    }

    @Override
    protected void inject() {
        if (frontRequestHandler == null) {
            this.frontRequestHandler = applicationContext.getBean(SpringFrontRequestHandler.class);
        }
    }
}
