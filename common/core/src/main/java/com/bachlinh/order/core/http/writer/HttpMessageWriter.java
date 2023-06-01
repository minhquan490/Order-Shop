package com.bachlinh.order.core.http.writer;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import com.bachlinh.order.utils.JacksonUtils;

import java.io.IOException;
import java.io.OutputStream;

class HttpMessageWriter implements MessageWriter {
    private static final Logger log = LogManager.getLogger(HttpMessageWriter.class);

    private final HttpServletResponse actualResponse;

    HttpMessageWriter(HttpServletResponse actualResponse) {
        this.actualResponse = actualResponse;
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
        byte[] data = JacksonUtils.writeObjectAsBytes(messageBody);
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
            log.warn("Can not write response to client");
            log.warn(e);
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
