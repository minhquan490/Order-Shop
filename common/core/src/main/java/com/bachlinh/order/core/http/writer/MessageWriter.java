package com.bachlinh.order.core.http.writer;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface MessageWriter {
    void writeHttpStatus(String status);

    void writeHttpStatus(int status);

    void writeHttpStatus(HttpStatus status);

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
