package com.bachlinh.order.core.http.writer;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.bachlinh.order.utils.JacksonUtils;

import java.io.IOException;
import java.io.OutputStream;

class HttpMessageWriter implements MessageWriter {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final int DEFAULT_STATUS = 200;
    private final HttpServletResponse actualResponse;

    HttpMessageWriter(HttpServletResponse actualResponse) {
        this.actualResponse = actualResponse;
    }

    @Override
    public void writeHttpStatus(String status) {
        try {
            int code = Integer.parseInt(status);
            writeHttpStatus(code);
        } catch (NumberFormatException e) {
            writeHttpStatus(DEFAULT_STATUS);
        }
    }

    @Override
    public void writeHttpStatus(int status) {
        if (status < 100) {
            this.actualResponse.setStatus(DEFAULT_STATUS);
        } else {
            this.actualResponse.setStatus(status);
        }
    }

    @Override
    public void writeHttpStatus(HttpStatus status) {
        if (status == null) {
            writeHttpStatus(DEFAULT_STATUS);
        } else {
            writeHttpStatus(status.value());
        }
    }

    @Override
    public void writeHeader(String headerName, Object headerValue) {
        this.actualResponse.setHeader(headerName, String.valueOf(headerValue));
    }

    @Override
    public void writeHeader(ResponseEntity<?> delegate) {
        writeHeader(delegate.getHeaders());
    }

    @Override
    public void writeHeader(HttpHeaders headers) {
        headers.forEach((s, strings) -> strings.forEach(v -> writeHeader(s, v)));
    }

    @Override
    public void writeMessage(Object messageBody) {
        if (messageBody == null) {
            return;
        }
        byte[] data;
        if (messageBody instanceof byte[] bytes) {
            data = bytes;
        } else {
            data = JacksonUtils.writeObjectAsBytes(messageBody);
        }
        writeMessage(data);
    }

    @Override
    public void writeMessage(byte[] messageBody) {
        try {
            writeDefaultHeader(messageBody.length);
            OutputStream outputStream = this.actualResponse.getOutputStream();
            outputStream.write(messageBody);
            outputStream.flush();
        } catch (IOException e) {
            log.warn("Can not write response to client", e);
        }
    }

    @Override
    public void writeMessage(ResponseEntity<?> delegate) {
        writeMessage(delegate.getBody());
    }

    private void writeDefaultHeader(int contentTypeLength) {
        this.actualResponse.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentTypeLength));
        this.actualResponse.setHeader(HttpHeaders.TRANSFER_ENCODING, "chucked");
    }
}
