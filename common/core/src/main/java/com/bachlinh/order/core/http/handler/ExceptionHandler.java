package com.bachlinh.order.core.http.handler;

import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.utils.JacksonUtils;
import com.bachlinh.order.utils.map.LinkedMultiValueMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public abstract class ExceptionHandler implements ThrowableHandler<Exception, NativeResponse<byte[]>> {

    @Override
    public NativeResponse<byte[]> handle(Exception throwable) {
        doOnException(throwable);
        var json = JacksonUtils.writeObjectAsBytes(new ExceptionReturn(status(), message(throwable)));
        NativeResponse.NativeResponseBuilder<byte[]> builder = NativeResponse.builder();
        NativeResponse<byte[]> response = builder
                .statusCode(status())
                .headers(new LinkedMultiValueMap<>())
                .body(json)
                .build();
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(json.length));
        return response;
    }

    protected abstract int status();

    protected abstract String[] message(Exception exception);

    protected abstract void doOnException(Exception exception);
}
