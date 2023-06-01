package com.bachlinh.order.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FrameworkServlet;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.core.http.writer.MessageWriter;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.web.handler.SpringFrontRequestHandler;

@ActiveReflection
public class WebServlet extends FrameworkServlet {

    private final transient SpringFrontRequestHandler frontRequestHandler;

    @ActiveReflection
    @DependenciesInitialize
    public WebServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
        new ApplicationScanner().findComponents();
        this.frontRequestHandler = webApplicationContext.getBean(SpringFrontRequestHandler.class);
        setEnableLoggingRequestDetails(true);
    }

    @Override
    protected void doService(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
        ResponseEntity<?> responseEntity = frontRequestHandler.handle(request, response);
        response.setStatus(responseEntity.getStatusCode().value());
        MessageWriter messageWriter = MessageWriter.httpMessageWriter(response);
        messageWriter.writeHeader(responseEntity);
        messageWriter.writeMessage(responseEntity);
    }
}
