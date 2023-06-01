package com.bachlinh.order.security.handler;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.bachlinh.order.exception.http.UnAuthorizationException;
import com.bachlinh.order.utils.JacksonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class UnAuthorizationHandler {

    public void onAuthenticationFailure(HttpServletResponse response, UnAuthorizationException exception) throws IOException {
        int code = HttpStatus.UNAUTHORIZED.value();
        Map<String, Object> res = new HashMap<>();
        res.put("message", exception.getMessage());
        res.put("code", code);
        response.setStatus(code);
        response.getOutputStream().write(JacksonUtils.writeObjectAsBytes(res));
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.flushBuffer();
    }
}
