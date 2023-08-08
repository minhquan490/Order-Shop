package com.bachlinh.order.security.handler;

import com.bachlinh.order.exception.http.AccessDeniedException;
import com.bachlinh.order.utils.JacksonUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class AccessDeniedHandler {

    public void handle(HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        int code = HttpStatus.FORBIDDEN.value();
        Map<String, Object> res = new HashMap<>();
        res.put("messages", new String[]{accessDeniedException.getMessage()});
        res.put("code", code);
        response.setStatus(code);
        response.getOutputStream().write(JacksonUtils.writeObjectAsBytes(res));
        response.flushBuffer();
    }
}
