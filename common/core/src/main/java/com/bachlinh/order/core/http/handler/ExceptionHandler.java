package com.bachlinh.order.core.http.handler;

import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.utils.JacksonUtils;
import com.bachlinh.order.utils.map.LinkedMultiValueMap;
import com.google.common.base.Objects;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.Serializable;
import java.util.Arrays;

public abstract class ExceptionHandler implements ThrowableHandler<Exception, NativeResponse<String>> {

    @Override
    public NativeResponse<String> handle(Exception throwable) {
        doOnException(throwable);
        String json = JacksonUtils.writeObjectAsString(new ExceptionReturn(status(), message(throwable)));
        NativeResponse.NativeResponseBuilder<String> builder = NativeResponse.builder();
        NativeResponse<String> response = builder
                .statusCode(status())
                .headers(new LinkedMultiValueMap<>())
                .body(json)
                .build();
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(json.length()));
        return response;
    }

    protected abstract int status();

    protected abstract String[] message(Exception exception);

    protected abstract void doOnException(Exception exception);

    private record ExceptionReturn(int status, String[] messages) implements Serializable {

        @Override
        public String toString() {
            return "ExceptionReturn{" +
                    "status=" + status +
                    ", messages=" + Arrays.toString(messages) +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExceptionReturn that = (ExceptionReturn) o;
            return status == that.status && Objects.equal(messages, that.messages);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(status, messages);
        }
    }
}
