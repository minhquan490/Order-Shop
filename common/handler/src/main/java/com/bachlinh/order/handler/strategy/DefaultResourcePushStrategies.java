package com.bachlinh.order.handler.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.PushBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.converter.spi.Converter;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.utils.JacksonUtils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

@Slf4j
class DefaultResourcePushStrategies implements ResourcePushStrategies {

    private static DefaultResourcePushStrategies instance;
    private final Converter<Collection<String>, NativeResponse<?>> converter;
    private final String appUrl;

    private DefaultResourcePushStrategies(Environment environment) {
        this.converter = new ResourceUrlConverter();
        this.appUrl = MessageFormat.format("https://{0}:{1}", environment.getProperty("server.address"), environment.getProperty("server.port"));
    }

    @Override
    public void pushResource(NativeResponse<?> response, HttpServletRequest request) {
        Collection<String> resourceUrls = converter.convert(response);
        PushBuilder pushBuilder = request.newPushBuilder();
        resourceUrls.forEach(resource -> {
            pushBuilder.method(HttpMethod.GET.name());
            pushBuilder.path(resource.replace(appUrl, ""));
            pushBuilder.push();
        });
    }

    static DefaultResourcePushStrategies getInstance(Environment environment) {
        if (instance == null) {
            instance = new DefaultResourcePushStrategies(environment);
        }
        return instance;
    }

    private static class ResourceUrlConverter implements Converter<Collection<String>, NativeResponse<?>> {

        @Override
        public Collection<String> convert(NativeResponse<?> message) {
            ObjectMapper mapper = JacksonUtils.getSingleton();
            Object body = message.getBody();
            JsonNode node;
            try {
                if (body instanceof String casted) {
                    node = mapper.readTree(casted);
                }
                if (body instanceof byte[] casted) {
                    node = mapper.readTree(casted);
                }
                node = mapper.readTree(JacksonUtils.writeObjectAsBytes(body));
            } catch (Exception e) {
                log.error("Can not convert response body", e);
                return Collections.emptyList();
            }
            return Stream.of(node.get("pictures").asText().split(",")).toList();
        }
    }
}
