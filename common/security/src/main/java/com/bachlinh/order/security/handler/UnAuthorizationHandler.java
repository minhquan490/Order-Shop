package com.bachlinh.order.security.handler;

import com.bachlinh.order.core.exception.http.UnAuthorizationException;
import com.bachlinh.order.core.utils.JacksonUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class UnAuthorizationHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void onAuthenticationFailure(HttpServletResponse response, UnAuthorizationException exception) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Un authorized cause", exception);
        }
        int code = HttpStatus.UNAUTHORIZED.value();
        Map<String, Object> res = HashMap.newHashMap(2);
        res.put("messages", new String[]{exception.getMessage()});
        res.put("code", code);
        response.setStatus(code);
        response.getOutputStream().write(JacksonUtils.writeObjectAsBytes(res));
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.flushBuffer();
    }
}
