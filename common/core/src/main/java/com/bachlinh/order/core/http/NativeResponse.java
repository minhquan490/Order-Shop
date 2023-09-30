package com.bachlinh.order.core.http;

import com.bachlinh.order.core.utils.map.LinkedMultiValueMap;
import com.bachlinh.order.core.utils.map.MultiValueMap;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NativeResponse<T> {

    @Nullable
    private final T body;

    private int statusCode;

    private boolean activePushBuilder;

    @Nullable
    private final MultiValueMap<String, String> headers;

    @Nullable
    private final NativeCookie[] cookies;

    NativeResponse(@Nullable T body, int statusCode, boolean activePushBuilder, @Nullable MultiValueMap<String, String> headers, @Nullable NativeCookie[] cookies) {
        this.body = body;
        this.statusCode = statusCode;
        this.activePushBuilder = activePushBuilder;
        this.headers = headers;
        this.cookies = cookies;
    }

    public static <T> NativeResponseBuilder<T> builder() {
        return new NativeResponseBuilder<>();
    }

    @SuppressWarnings("unchecked")
    public NativeResponse<T> merge(NativeResponse<T> other) {
        var builder = NativeResponse.builder();
        MultiValueMap<String, String> mergedHeaders = this.getHeaders() == null ? new LinkedMultiValueMap<>() : this.getHeaders();
        mergedHeaders.putAll(other.getHeaders() == null ? Collections.emptyMap() : other.getHeaders());
        builder.headers(mergedHeaders);
        builder.body(other.getBody());
        List<NativeCookie> mergedCookies = this.getCookies() == null ? new ArrayList<>() : Arrays.asList(this.getCookies());
        mergedCookies.addAll(other.getCookies() == null ? new ArrayList<>() : Arrays.asList(other.getCookies()));
        builder.cookies(mergedCookies.toArray(new NativeCookie[0]));
        builder.statusCode(other.getStatusCode());
        builder.activePushBuilder(other.isActivePushBuilder());
        return (NativeResponse<T>) builder.build();
    }

    public void addHeader(String name, String value) {
        if (headers == null) {
            return;
        }
        headers.compute(name, (s, strings) -> {
            if (strings == null) {
                strings = new LinkedList<>();
            }
            strings.add(value);
            return strings;
        });
    }

    @Nullable
    public T getBody() {
        return this.body;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public boolean isActivePushBuilder() {
        return this.activePushBuilder;
    }

    @Nullable
    public MultiValueMap<String, String> getHeaders() {
        return this.headers;
    }

    @Nullable
    public NativeCookie[] getCookies() {
        return this.cookies;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setActivePushBuilder(boolean activePushBuilder) {
        this.activePushBuilder = activePushBuilder;
    }

    public static class NativeResponseBuilder<T> {
        private T body;
        private int statusCode;
        private boolean activePushBuilder;
        private MultiValueMap<String, String> headers;
        private NativeCookie[] cookies;

        NativeResponseBuilder() {
        }

        public NativeResponseBuilder<T> body(T body) {
            this.body = body;
            return this;
        }

        public NativeResponseBuilder<T> statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public NativeResponseBuilder<T> activePushBuilder(boolean activePushBuilder) {
            this.activePushBuilder = activePushBuilder;
            return this;
        }

        public NativeResponseBuilder<T> headers(MultiValueMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public NativeResponseBuilder<T> cookies(NativeCookie[] cookies) {
            this.cookies = cookies;
            return this;
        }

        public NativeResponse<T> build() {
            return new NativeResponse<>(this.body, this.statusCode, this.activePushBuilder, this.headers, this.cookies);
        }

        public String toString() {
            return "NativeResponse.NativeResponseBuilder(body=" + this.body + ", statusCode=" + this.statusCode + ", activePushBuilder=" + this.activePushBuilder + ", headers=" + this.headers + ", cookies=" + Arrays.deepToString(this.cookies) + ")";
        }
    }
}
