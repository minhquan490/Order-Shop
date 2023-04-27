package com.bachlinh.order.core.http.converter.spi;

import com.bachlinh.order.core.http.converter.internal.JavaBaseResponseConverterImpl;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.http.HttpResponse;

public interface JavaBaseResponseConverter extends Converter<JsonNode, HttpResponse<byte[]>> {

    static JavaBaseResponseConverter defaultConverter() {
        return JavaBaseResponseConverterImpl.getInstance();
    }
}
