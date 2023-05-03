package com.bachlinh.order.core.tcp.messaging;

import org.springframework.web.socket.WebSocketMessage;
import com.bachlinh.order.utils.JacksonUtils;

public class JsonMessage implements WebSocketMessage<String> {
    private final String payload;

    private JsonMessage(Object payload) {
        this.payload = JacksonUtils.writeObjectAsString(payload);
    }

    public static JsonMessage convert(Object message) {
        return new JsonMessage(message);
    }

    @Override
    public String getPayload() {
        return payload;
    }

    @Override
    public int getPayloadLength() {
        return payload.length();
    }

    @Override
    public boolean isLast() {
        return false;
    }
}
