package com.bachlinh.order.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class NativeMethodHandleRequestMetadataReader {
    private static final NativeMethodHandleRequestMetadataReader SINGLETON = new NativeMethodHandleRequestMetadataReader();
    private final Map<String, MetadataReader> nativeMethodMetadataMap = new LinkedHashMap<>();

    public void defineMetadata(String path, Method handlerMethod) {
        var metadata = new MetadataReader(handlerMethod.getParameterTypes()[0], handlerMethod.getReturnType());
        nativeMethodMetadataMap.put(path, metadata);
    }

    public MetadataReader getNativeMethodMetadata(String path) {
        return nativeMethodMetadataMap.get(path);
    }

    public record MetadataReader(Class<?> parameterType, Class<?> returnTypes) {
    }

    public static NativeMethodHandleRequestMetadataReader getInstance() {
        return SINGLETON;
    }
}
