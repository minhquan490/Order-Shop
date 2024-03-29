package com.bachlinh.order.http.writer;

import com.bachlinh.order.http.parser.spi.NettyHttpConvention;
import com.bachlinh.order.core.utils.map.MultiValueMap;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface MessageWriter extends NettyHttpConvention {
    void writeHttpStatus(String status);

    void writeHttpStatus(int status);

    void writeHttpStatus(HttpStatus status);

    void writeHeader(String headerName, Object headerValue);

    void writeHeader(ResponseEntity<?> delegate);

    void writeHeader(HttpHeaders headers);

    void writeHeader(MultiValueMap<String, String> headers);

    void writeMessage(Object messageBody);

    void writeMessage(byte[] messageBody);

    void writeMessage(ResponseEntity<?> delegate);

    static MessageWriter httpMessageWriter(HttpServletResponse response) {
        return new HttpMessageWriter(response);
    }
}
