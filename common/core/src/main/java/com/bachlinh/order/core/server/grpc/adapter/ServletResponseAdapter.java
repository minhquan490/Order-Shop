package com.bachlinh.order.core.server.grpc.adapter;

import com.bachlinh.order.core.server.grpc.InboundMessage;
import com.bachlinh.order.core.server.grpc.OutboundMessage;
import com.bachlinh.order.utils.JacksonUtils;
import com.google.protobuf.ByteString;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import io.netty.handler.codec.http.HttpHeaderNames;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public class ServletResponseAdapter implements HttpServletResponse {
    private static final String SPLITTER = " {cookie} ";
    private final OutboundMessage.Builder builder = OutboundMessage.newBuilder();
    private final ServerCall<InboundMessage, OutboundMessage> call;

    public ServletResponseAdapter(ServerCall<InboundMessage, OutboundMessage> call) {
        this.call = call;
    }

    @Override
    public void addCookie(Cookie cookie) {
        String cookieHeader = builder.getHeadersOrDefault(HttpHeaderNames.COOKIE.toString(), null);
        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append(MessageFormat.format("{0}={1}; ", cookie.getName(), cookie.getValue()));
        cookieBuilder.append(MessageFormat.format("path={0}; ", cookie.getPath()));
        cookieBuilder.append(MessageFormat.format("expires={0}; ", new Date(cookie.getMaxAge())));
        if (cookie.getSecure()) {
            cookieBuilder.append("secure; ");
        }
        if (cookie.isHttpOnly()) {
            cookieBuilder.append("HttpOnly; ");
        }
        cookieBuilder.append(MessageFormat.format("SameSite={0}", cookie.getAttribute("SameSite")));
        if (cookieHeader == null) {
            builder.putHeaders(HttpHeaderNames.SET_COOKIE.toString(), cookieBuilder.toString());
        } else {
            builder.putHeaders(HttpHeaderNames.SET_COOKIE.toString(), cookieBuilder.append(SPLITTER).append(cookieHeader).toString());
        }
    }

    @Override
    public boolean containsHeader(String name) {
        return builder.containsHeaders(name);
    }

    @Override
    public String encodeURL(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

    @Override
    public String encodeRedirectURL(String url) {
        return encodeURL(url);
    }

    @Override
    public void sendError(int sc, String msg) {
        builder.setStatus(sc);
        builder.setBody(ByteString.copyFrom(JacksonUtils.writeObjectAsBytes(msg)));
        call.sendMessage(convert());
        call.close(Status.UNKNOWN, new Metadata());
    }

    @Override
    public void sendError(int sc) {
        sendError(sc, "");
    }

    @Override
    public void sendRedirect(String location) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDateHeader(String name, long date) {
        builder.putHeaders(HttpHeaderNames.DATE.toString(), new Date(date).toString());
    }

    @Override
    public void addDateHeader(String name, long date) {
        setDateHeader(name, date);
    }

    @Override
    public void setHeader(String name, String value) {
        builder.putHeaders(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        setHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addIntHeader(String name, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStatus(int sc) {
        builder.setStatus(sc);
    }

    @Override
    public int getStatus() {
        return builder.getStatus();
    }

    @Override
    public String getHeader(String name) {
        return builder.getHeadersOrDefault(name, null);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return builder.getHeadersMap().keySet();
    }

    @Override
    public String getCharacterEncoding() {
        return StandardCharsets.UTF_8.displayName();
    }

    @Override
    public String getContentType() {
        return builder.getHeadersOrDefault(HttpHeaderNames.CONTENT_LENGTH.toString(), null);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                // Do nothing
            }

            @Override
            public void write(int b) {
                outputStream.write(b);
            }

            @Override
            public void flush() throws IOException {
                builder.setBody(ByteString.copyFrom(outputStream.toByteArray()));
                call.sendMessage(convert());
                call.close(Status.OK, new Metadata());
                outputStream.flush();
            }
        };
    }

    @Override
    public PrintWriter getWriter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterEncoding(String charset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContentLength(int len) {
        builder.putHeaders(HttpHeaderNames.CONTENT_LENGTH.toString(), String.valueOf(len));
    }

    @Override
    public void setContentLengthLong(long len) {
        builder.putHeaders(HttpHeaderNames.CONTENT_LENGTH.toString(), String.valueOf(len));
    }

    @Override
    public void setContentType(String type) {
        builder.putHeaders(HttpHeaderNames.CONTENT_TYPE.toString(), type);
    }

    @Override
    public void setBufferSize(int size) {
        // Do nothing
    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        getOutputStream().flush();
    }

    @Override
    public void resetBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCommitted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLocale(Locale loc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException();
    }

    public OutboundMessage convert() {
        return builder.build();
    }
}
