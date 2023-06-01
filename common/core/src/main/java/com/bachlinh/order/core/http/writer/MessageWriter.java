package com.bachlinh.order.core.http.writer;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public interface MessageWriter {
    void writeHeader(String headerName, Object headerValue);

    void writeHeader(ResponseEntity<?> delegate);

    void writeHeader(HttpHeaders headers);

    void writeMessage(Object messageBody);

    void writeMessage(byte[] messageBody);

    void writeMessage(ResponseEntity<?> delegate);

    static MessageWriter httpMessageWriter(HttpServletResponse response) {
        return new HttpMessageWriter(response);
    }
}
