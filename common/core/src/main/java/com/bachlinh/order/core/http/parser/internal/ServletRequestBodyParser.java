package com.bachlinh.order.core.http.parser.internal;

import com.bachlinh.order.core.http.parser.spi.RequestBodyParser;
import com.bachlinh.order.exception.system.CriticalException;
import com.bachlinh.order.utils.JacksonUtils;
import jakarta.servlet.ServletRequest;

import java.io.IOException;

public class ServletRequestBodyParser implements RequestBodyParser<ServletRequest> {
    @Override
    @SuppressWarnings("unchecked")
    public <U> U parseRequest(ServletRequest request, Class<?> expectedType) {
        try {
            byte[] data = request.getInputStream().readAllBytes();
            if (data.length == 0) {
                return null;
            }
            return (U) JacksonUtils.getSingleton().readValue(data, expectedType);
        } catch (IOException e) {
            throw new CriticalException("Can not parse request", e);
        }
    }
}
