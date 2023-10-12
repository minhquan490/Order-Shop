package com.bachlinh.order.core;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class NativeMethodHandleRequestMetadataReader {
    private static final String WILD_CARD_CHARACTER = "?";
    private static final NativeMethodHandleRequestMetadataReader SINGLETON = new NativeMethodHandleRequestMetadataReader();
    private final Map<String, MetadataReader> nativeMethodMetadataMap = new LinkedHashMap<>();

    private final String wildCard = WILD_CARD_CHARACTER;

    public void defineMetadata(String path, Method handlerMethod) {
        Type[] genericTypes = ((ParameterizedType) handlerMethod.getDeclaringClass().getGenericSuperclass()).getActualTypeArguments();
        Class<?> returnType = verifyType(genericTypes[0]);
        Class<?> paramType = verifyType(genericTypes[1]);
        var metadata = new MetadataReader(paramType, returnType);
        nativeMethodMetadataMap.put(path, metadata);
    }

    public MetadataReader getNativeMethodMetadata(String path) {
        return nativeMethodMetadataMap.get(path);
    }

    private Class<?> verifyType(Type type) {
        if (!(type instanceof ParameterizedType parameterizedType)) {
            return (Class<?>) type;
        }
        boolean hasWildCard = Stream.of(parameterizedType.getActualTypeArguments()).anyMatch(t -> t.getTypeName().equals(wildCard));
        if (hasWildCard) {
            return (Class<?>) parameterizedType.getRawType();
        } else {
            return verifyType(parameterizedType.getActualTypeArguments()[0]);
        }
    }

    public record MetadataReader(Class<?> parameterType, Class<?> returnTypes) {
    }

    public static NativeMethodHandleRequestMetadataReader getInstance() {
        return SINGLETON;
    }
}

