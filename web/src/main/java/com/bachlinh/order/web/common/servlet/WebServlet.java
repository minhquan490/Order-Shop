package com.bachlinh.order.web.common.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FrameworkServlet;

public class WebServlet extends FrameworkServlet {

    private final transient ServletRouter servletRouter;

    public WebServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
        this.servletRouter = webApplicationContext.getBean(ServletRouter.class);
        setEnableLoggingRequestDetails(true);
    }

    @Override
    protected void doService(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
        servletRouter.handleRequest(request, response);
    }
}
