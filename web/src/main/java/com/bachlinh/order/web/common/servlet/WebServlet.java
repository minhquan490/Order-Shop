package com.bachlinh.order.web.common.servlet;

import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.handler.router.ServletRouter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.util.WebUtils;

public class WebServlet extends FrameworkServlet {

    private final transient ServletRouter servletRouter;
    private final transient MultipartResolver multipartResolver = new StandardServletMultipartResolver();

    public WebServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
        this.servletRouter = webApplicationContext.getBean(ServletRouter.class);
        setEnableLoggingRequestDetails(true);
    }

    @Override
    protected void doService(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
        HttpServletRequest processedRequest = request;
        boolean multipartRequestParsed = false;

        try {
            processedRequest = checkMultipart(request);
            multipartRequestParsed = (processedRequest != request);
            servletRouter.handleRequest(processedRequest, response);
        } catch (MultipartException e) {
            throw new CriticalException("Error when handle multipart", e);
        } finally {
            if (multipartRequestParsed) {
                cleanupMultipart(processedRequest);
            }
        }
    }

    private HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
        if (this.multipartResolver.isMultipart(request)) {
            return this.multipartResolver.resolveMultipart(request);
        }
        return request;
    }

    private void cleanupMultipart(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest =
                WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        if (multipartRequest != null) {
            this.multipartResolver.cleanupMultipart(multipartRequest);
        }
    }
}
