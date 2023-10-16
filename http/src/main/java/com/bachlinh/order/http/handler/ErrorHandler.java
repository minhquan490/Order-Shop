package com.bachlinh.order.http.handler;

import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.core.utils.JacksonUtils;
import com.bachlinh.order.core.utils.map.LinkedMultiValueMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public abstract class ErrorHandler implements ThrowableHandler<Error, NativeResponse<byte[]>> {

    @Override
    public NativeResponse<byte[]> handle(Error throwable) {
        executeOnError(throwable);
        Map<String, Object> error = HashMap.newHashMap(2);
        error.put("status", HttpStatus.SERVICE_UNAVAILABLE);
        error.put("messages", new String[]{"Problem when process your request, please contact to the admin"});
        NativeResponse.NativeResponseBuilder<byte[]> builder = NativeResponse.builder();
        var json = JacksonUtils.writeObjectAsBytes(error);
        NativeResponse<byte[]> response = builder
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .headers(new LinkedMultiValueMap<>())
                .body(json)
                .build();
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(json.length));
        return response;
    }

    protected abstract void executeOnError(Error error);
}
