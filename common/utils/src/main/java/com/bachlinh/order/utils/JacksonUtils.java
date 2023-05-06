package com.bachlinh.order.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bachlinh.order.exception.system.common.JsonConvertException;

public final class JacksonUtils {
    private JacksonUtils() {
    }

    private static final ObjectMapper SINGLETON = new ObjectMapper();

    public static byte[] writeObjectAsBytes(Object value) {
        try {
            return SINGLETON.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new JsonConvertException("Can not convert [" + value.getClass().getSimpleName() + "] to json object", e);
        }
    }

    public static String writeObjectAsString(Object value) {
        try {
            return SINGLETON.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new JsonConvertException("Can not convert object to json");
        }
    }

    public static ObjectMapper getSingleton() {
        return SINGLETON;
    }
}
