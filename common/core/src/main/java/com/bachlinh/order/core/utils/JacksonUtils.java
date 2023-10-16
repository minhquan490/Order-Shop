package com.bachlinh.order.core.utils;

import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.exception.system.common.JsonConvertException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

public final class JacksonUtils {
    private JacksonUtils() {
    }

    private static final ObjectMapper SINGLETON = new ObjectMapper();

    public static byte[] writeObjectAsBytes(Object value) {
        try {
            return SINGLETON.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            String message = STR. "Can not convert [\{ value.getClass().getSimpleName() }] to json object" ;
            throw new JsonConvertException(message, e);
        }
    }

    public static String writeObjectAsString(Object value) {
        if (value == null) {
            return "";
        }
        try {
            return SINGLETON.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new JsonConvertException("Can not convert object to json");
        }
    }

    public static JsonNode readJsonFile(URL url) {
        try {
            return SINGLETON.readTree(url);
        } catch (IOException e) {
            String message = STR. "Can not read json from url [\{ url.getPath() }]" ;
            throw new CriticalException(message);
        }
    }

    public static Object deserialize(String json) {
        try {
            return SINGLETON.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            throw new CriticalException(e);
        }
    }

    public static ObjectMapper getSingleton() {
        return SINGLETON;
    }
}
