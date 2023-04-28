package com.bachlinh.order.core.http.handler;

import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.utils.JacksonUtils;
import com.bachlinh.order.utils.map.LinkedMultiValueMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public abstract class ErrorHandler implements ThrowableHandler<Error, NativeResponse<String>> {

    @Override
    public NativeResponse<String> handle(Error throwable) {
        executeOnError(throwable);
        Map<String, Object> error = new HashMap<>(2);
        error.put("status", HttpStatus.SERVICE_UNAVAILABLE);
        error.put("message", "Problem when process your request, please contact to the admin");
        NativeResponse.NativeResponseBuilder<String> builder = NativeResponse.builder();
        String json = JacksonUtils.writeObjectAsString(error);
        NativeResponse<String> response = builder
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .headers(new LinkedMultiValueMap<>())
                .body(json)
                .build();
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(json.length()));
        return response;
    }

    protected abstract void executeOnError(Error error);
}
