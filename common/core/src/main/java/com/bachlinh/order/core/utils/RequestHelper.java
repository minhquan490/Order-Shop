package com.bachlinh.order.core.utils;

import com.bachlinh.order.core.http.NativeRequest;
import org.springframework.core.NamedThreadLocal;

public final class RequestHelper {

    private static final ThreadLocal<NativeRequest<?>> NATIVE_REQUEST_THREAD_LOCAL = new NamedThreadLocal<>("RequestLocal");

    private RequestHelper() {
    }

    public static void bindToThread(NativeRequest<?> currentRequest) {
        NATIVE_REQUEST_THREAD_LOCAL.set(currentRequest);
    }

    public static void release() {
        NATIVE_REQUEST_THREAD_LOCAL.remove();
    }

    public static NativeRequest<?> getCurrentRequest() {
        return NATIVE_REQUEST_THREAD_LOCAL.get();
    }
}
