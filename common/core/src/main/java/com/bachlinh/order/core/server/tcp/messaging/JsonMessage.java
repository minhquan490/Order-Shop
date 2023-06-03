package com.bachlinh.order.core.server.tcp.messaging;

import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketMessage;
import com.bachlinh.order.utils.JacksonUtils;

public class JsonMessage implements WebSocketMessage<String> {
    private final String payload;

    private JsonMessage(@NonNull Object payload) {
        this.payload = JacksonUtils.writeObjectAsString(payload);
    }

    public static JsonMessage convert(@NonNull Object message) {
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
