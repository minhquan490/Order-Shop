package com.bachlinh.order.web.common.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FrameworkServlet;
import com.bachlinh.order.core.http.writer.MessageWriter;
import com.bachlinh.order.web.handler.SpringFrontRequestHandler;

public class WebServlet extends FrameworkServlet {

    private final transient SpringFrontRequestHandler frontRequestHandler;

    public WebServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
        this.frontRequestHandler = webApplicationContext.getBean(SpringFrontRequestHandler.class);
        setEnableLoggingRequestDetails(true);
    }

    @Override
    protected void doService(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
        ResponseEntity<?> responseEntity = frontRequestHandler.handle(request, response);
        MessageWriter messageWriter = MessageWriter.httpMessageWriter(response);
        messageWriter.writeHttpStatus(responseEntity.getStatusCode().value());
        messageWriter.writeHeader(responseEntity);
        messageWriter.writeMessage(responseEntity);
    }
}
