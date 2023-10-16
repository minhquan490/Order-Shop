package com.bachlinh.order.http.writer;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.incubator.codec.http3.Http3DataFrame;
import io.netty.incubator.codec.http3.Http3HeadersFrame;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bachlinh.order.http.server.channel.adapter.NettyServletResponseAdapter;
import com.bachlinh.order.core.utils.JacksonUtils;
import com.bachlinh.order.core.utils.map.MultiValueMap;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class HttpMessageWriter implements MessageWriter {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final int DEFAULT_STATUS = 200;
    private final HttpServletResponse actualResponse;

    HttpMessageWriter(HttpServletResponse actualResponse) {
        this.actualResponse = actualResponse;
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
    public void writeHeader(String headerName, Object headerValue) {
        var headerContained = this.actualResponse.containsHeader(headerName);
        if (headerContained) {
            var value = String.valueOf(headerValue);
            var headers = this.actualResponse.getHeaders(headerName);
            if (!headers.contains(value)) {
                this.actualResponse.addHeader(headerName, value);
            }
        } else {
            this.actualResponse.setHeader(headerName, String.valueOf(headerValue));
        }
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
    public void writeHeader(MultiValueMap<String, String> headers) {
        headers.forEach((s, strings) -> strings.forEach(s1 -> writeHeader(s, s1)));
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

    @Override
    public Http3DataFrame toH3DataFrame() {
        if (actualResponse instanceof NettyServletResponseAdapter adapter) {
            return adapter.toH3DataFrame();
        } else {
            return null;
        }
    }

    @Override
    public Http3DataFrame[] toH3DataFrames(long frameSize) {
        if (actualResponse instanceof NettyServletResponseAdapter adapter) {
            return adapter.toH3DataFrames(frameSize);
        } else {
            return new Http3DataFrame[0];
        }
    }

    @Override
    public Http3HeadersFrame toH3HeaderFrame() {
        if (actualResponse instanceof NettyServletResponseAdapter adapter) {
            return adapter.toH3HeaderFrame();
        } else {
            return null;
        }
    }

    @Override
    public Http2DataFrame toH2DataFrame() {
        if (actualResponse instanceof NettyServletResponseAdapter adapter) {
            return adapter.toH2DataFrame();
        } else {
            return null;
        }
    }

    @Override
    public Http2DataFrame toH2DataFrame(Http2FrameStream stream) {
        return null;
    }

    @Override
    public Http2DataFrame[] toH2DataFrame(Http2FrameStream stream, long frameSize) {
        if (actualResponse instanceof NettyServletResponseAdapter adapter) {
            return adapter.toH2DataFrame(stream, frameSize);
        } else {
            return new Http2DataFrame[0];
        }
    }

    @Override
    public Http2DataFrame[] toH2DataFrame(long frameSize) {
        if (actualResponse instanceof NettyServletResponseAdapter adapter) {
            return adapter.toH2DataFrame(frameSize);
        } else {
            return new Http2DataFrame[0];
        }
    }

    @Override
    public Http2HeadersFrame toH2HeaderFrame() {
        if (actualResponse instanceof NettyServletResponseAdapter adapter) {
            return adapter.toH2HeaderFrame();
        } else {
            return null;
        }
    }

    @Override
    public Http2HeadersFrame toH2HeaderFrame(Http2FrameStream stream) {
        if (actualResponse instanceof NettyServletResponseAdapter adapter) {
            return adapter.toH2HeaderFrame(stream);
        } else {
            return null;
        }
    }

    @Override
    public FullHttpResponse toFullHttpResponse() {
        if (actualResponse instanceof NettyServletResponseAdapter adapter) {
            return adapter.toFullHttpResponse();
        } else {
            return null;
        }
    }
}
