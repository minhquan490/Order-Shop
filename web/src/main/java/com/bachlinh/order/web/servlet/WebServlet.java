package com.bachlinh.order.web.servlet;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.utils.JacksonUtils;
import com.bachlinh.order.web.handler.SpringFrontRequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FrameworkServlet;

import java.nio.charset.StandardCharsets;

@Component("dispatcherServlet")
@ActiveReflection
public class WebServlet extends FrameworkServlet {

    private final transient SpringFrontRequestHandler frontRequestHandler;
    private final ObjectMapper objectMapper = JacksonUtils.getSingleton();

    @ActiveReflection
    @Autowired
    public WebServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
        new ApplicationScanner().findComponents();
        this.frontRequestHandler = webApplicationContext.getBean(SpringFrontRequestHandler.class);
    }

    @Override
    protected void doService(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) throws Exception {
        ResponseEntity<?> responseEntity = frontRequestHandler.handle(request, response);
        response.setStatus(responseEntity.getStatusCode().value());
        responseEntity.getHeaders().forEach((s, strings) -> strings.forEach(s1 -> response.addHeader(s, s1)));
        String json = objectMapper.writeValueAsString(responseEntity.getBody());
        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
        response.flushBuffer();
    }

    @Override
    protected void onRefresh(ApplicationContext context) {
        super.onRefresh(context);
    }
}
