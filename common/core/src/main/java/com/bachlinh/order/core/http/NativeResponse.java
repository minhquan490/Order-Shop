package com.bachlinh.order.core.http;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import com.bachlinh.order.utils.map.LinkedMultiValueMap;
import com.bachlinh.order.utils.map.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Builder
@Getter
@Setter
public class NativeResponse<T> {

    @Nullable
    private final T body;

    private int statusCode;

    private boolean activePushBuilder;

    @Nullable
    private final MultiValueMap<String, String> headers;

    @Nullable
    private final NativeCookie[] cookies;

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
        if (headers.get(name) == null) {
            headers.put(name, Collections.singletonList(value));
            return;
        }
        headers.set(name, value);
    }

}
