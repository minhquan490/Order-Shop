package com.bachlinh.order.core.http.converter.internal;

import com.bachlinh.order.core.http.converter.spi.JavaBaseResponseConverter;
import com.bachlinh.order.core.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import java.io.IOException;
import java.net.http.HttpResponse;

public class JavaBaseResponseConverterImpl implements JavaBaseResponseConverter {
    private static final JavaBaseResponseConverter INSTANCE = new JavaBaseResponseConverterImpl();

    private final ObjectMapper mapper = JacksonUtils.getSingleton();

    private JavaBaseResponseConverterImpl() {
    }

    @Override
    public JsonNode convert(HttpResponse<byte[]> message) {
        try {
            byte[] resp = message.body();
            return mapper.readTree(resp);
        } catch (IOException e) {
            return NullNode.getInstance();
        }
    }

    public static JavaBaseResponseConverter getInstance() {
        return INSTANCE;
    }
}
