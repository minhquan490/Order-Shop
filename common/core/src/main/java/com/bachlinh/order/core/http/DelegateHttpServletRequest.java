package com.bachlinh.order.core.http;

import com.bachlinh.order.exception.system.common.CriticalException;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DelegateHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] requestBodyData;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request the {@link HttpServletRequest} to be wrapped.
     * @throws IllegalArgumentException if the request is null
     */
    public DelegateHttpServletRequest(HttpServletRequest request) {
        super(request);
        try {
            this.requestBodyData = request.getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new CriticalException("Can not wrap servlet request", e);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBodyData);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return !isReady();
            }

            @Override
            public boolean isReady() {
                return requestBodyData.length != 0;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Do nothing
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }
}
