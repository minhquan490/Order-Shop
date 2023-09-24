package com.bachlinh.order.core.server.netty.channel.adapter;

import com.bachlinh.order.core.http.parser.spi.NettyHttpConvention;
import com.bachlinh.order.utils.map.LinkedMultiValueMap;
import com.bachlinh.order.utils.map.MultiValueMap;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.incubator.codec.http3.DefaultHttp3DataFrame;
import io.netty.incubator.codec.http3.DefaultHttp3Headers;
import io.netty.incubator.codec.http3.DefaultHttp3HeadersFrame;
import io.netty.incubator.codec.http3.Http3DataFrame;
import io.netty.incubator.codec.http3.Http3HeadersFrame;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.UrlEncoded;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NettyServletResponseAdapter implements HttpServletResponse, NettyHttpConvention {
    private static final ServerCookieEncoder COOKIE_ENCODER = ServerCookieEncoder.LAX;
    private final MultiValueMap<String, Object> headers = new LinkedMultiValueMap<>();
    private byte[] bodyData;
    private int status = 200;
    private boolean committed = false;

    private NettyServletResponseAdapter() {
    }

    public static HttpServletResponse getInstance() {
        return new NettyServletResponseAdapter();
    }

    @Override
    public FullHttpResponse toFullHttpResponse() {
        var resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status), Unpooled.copiedBuffer(bodyData));
        headers.forEach((s, objects) -> resp.headers().set(s, objects));
        return resp;
    }

    @Override
    public Http2HeadersFrame toH2HeaderFrame() {
        var defaultH2Header = new DefaultHttp2Headers();
        defaultH2Header.status(String.valueOf(status));
        Set<Map.Entry<String, List<Object>>> filteredHeaders = filterHeaders(headers.entrySet());
        for (Map.Entry<String, List<Object>> entry : filteredHeaders) {
            defaultH2Header.set(entry.getKey().toLowerCase(), entry.getValue().stream().map(Object::toString).toList());
        }
        return new DefaultHttp2HeadersFrame(defaultH2Header);
    }

    @Override
    public Http2HeadersFrame toH2HeaderFrame(Http2FrameStream stream) {
        return ((DefaultHttp2HeadersFrame) toH2HeaderFrame()).stream(stream);
    }

    @Override
    public Http2DataFrame toH2DataFrame() {
        long size = bodyData == null ? 0 : bodyData.length;
        return toH2DataFrame(size)[0];
    }

    @Override
    public Http2DataFrame toH2DataFrame(Http2FrameStream stream) {
        return ((DefaultHttp2DataFrame) toH2DataFrame()).stream(stream);
    }

    @Override
    public Http2DataFrame[] toH2DataFrame(Http2FrameStream stream, long frameSize) {
        byte[] data = bodyData;
        if (data == null) {
            return new Http2DataFrame[0];
        }
        int part = (int) (data.length / frameSize);
        Deque<Http2DataFrame> frames = new LinkedList<>();
        for (int i = 0; i < part; i++) {
            byte[] chunk = Arrays.copyOfRange(data, (int) (i * frameSize), (int) ((i * frameSize) + frameSize));
            Http2DataFrame dataFrame = new DefaultHttp2DataFrame(Unpooled.copiedBuffer(chunk)).stream(stream);
            frames.add(dataFrame);
        }
        int remain = (int) (data.length % frameSize);
        if (remain > 0) {
            byte[] chunk = Arrays.copyOfRange(data, data.length - remain, data.length);
            Http2DataFrame dataFrame = new DefaultHttp2DataFrame(Unpooled.copiedBuffer(chunk)).stream(stream);
            frames.add(dataFrame);
        }
        Http2DataFrame lastFrame = frames.pollLast();
        if (lastFrame == null) {
            return new Http2DataFrame[0];
        }
        Http2DataFrame newLastFrame = new DefaultHttp2DataFrame(lastFrame.content(), true).stream(stream);
        frames.addLast(newLastFrame);
        return frames.toArray(new Http2DataFrame[0]);
    }

    @Override
    public Http2DataFrame[] toH2DataFrame(long frameSize) {
        return toH2DataFrame(null, frameSize);
    }

    @Override
    public Http3HeadersFrame toH3HeaderFrame() {
        var defaultH3Header = new DefaultHttp3Headers();
        defaultH3Header.status(String.valueOf(status));
        for (var entry : filterHeaders(headers.entrySet())) {
            defaultH3Header.set(entry.getKey(), entry.getValue().stream().map(Object::toString).toList());
        }
        return new DefaultHttp3HeadersFrame(defaultH3Header);
    }

    @Override
    public Http3DataFrame toH3DataFrame() {
        long size = bodyData == null ? 0 : bodyData.length;
        return toH3DataFrames(size)[0];
    }

    @Override
    public Http3DataFrame[] toH3DataFrames(long frameSize) {
        byte[] data = bodyData;
        if (data == null) {
            return new Http3DataFrame[0];
        }
        int part = (int) (data.length / frameSize);
        Deque<Http3DataFrame> frames = new LinkedList<>();
        for (int i = 0; i < part; i++) {
            byte[] chunk = Arrays.copyOfRange(data, (int) (i * frameSize), (int) ((i * frameSize) + frameSize));
            Http3DataFrame dataFrame = new DefaultHttp3DataFrame(Unpooled.copiedBuffer(chunk));
            frames.add(dataFrame);
        }
        int remain = (int) (data.length % frameSize);
        if (remain > 0) {
            byte[] chunk = Arrays.copyOfRange(data, data.length - remain, data.length);
            Http3DataFrame dataFrame = new DefaultHttp3DataFrame(Unpooled.copiedBuffer(chunk));
            frames.add(dataFrame);
        }
        return frames.toArray(new Http3DataFrame[0]);
    }

    @Override
    public void addCookie(Cookie cookie) {
        headers.compute(HttpHeader.SET_COOKIE.toString(), (s, strings) -> {
            if (strings == null) {
                strings = new LinkedList<>();
            }
            var nettyCookie = new DefaultCookie(cookie.getName(), cookie.getValue());
            nettyCookie.setDomain(cookie.getDomain());
            nettyCookie.setHttpOnly(cookie.isHttpOnly());
            nettyCookie.setPath(cookie.getPath());
            nettyCookie.setSecure(cookie.getSecure());
            nettyCookie.setMaxAge(cookie.getMaxAge());
            nettyCookie.setSameSite(CookieHeaderNames.SameSite.Lax);
            var setCookie = COOKIE_ENCODER.encode(nettyCookie);
            strings.add(setCookie);
            return strings;
        });
    }

    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public String encodeURL(String url) {
        return UrlEncoded.encodeString(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
        return UrlEncoded.encodeString(url);
    }

    @Override
    public void sendError(int sc, String msg) {
        status = sc;
        this.bodyData = msg.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void sendError(int sc) {
        status = sc;
    }

    @Override
    public void sendRedirect(String location) {
        // Do nothing
    }

    @Override
    public void setDateHeader(String name, long date) {
        computed(HttpHeader.DATE.asString(), date);
    }

    @Override
    public void addDateHeader(String name, long date) {
        computed(HttpHeader.DATE.asString(), date);
    }

    @Override
    public void setHeader(String name, String value) {
        computed(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        computed(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        computed(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        computed(name, value);
    }

    @Override
    public void setStatus(int sc) {
        status = sc;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getHeader(String name) {
        var headerList = headers.get(name);
        if (headerList == null || headerList.isEmpty()) {
            return null;
        }
        return String.valueOf(headerList.get(0));
    }

    @Override
    public Collection<String> getHeaders(String name) {
        var headerList = headers.get(name);
        return headerList == null ? Collections.emptyList() : headerList.stream().map(String::valueOf).toList();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        var headerList = headers.get(HttpHeader.CONTENT_TYPE.asString());
        if (headerList == null || headerList.isEmpty()) {
            return MediaType.APPLICATION_JSON_VALUE;
        }
        return String.valueOf(headerList.get(0));
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            private byte[] data;

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void write(int b) {
                //Do nothing
            }

            @Override
            public void write(@NonNull byte[] b) {
                data = b;
            }

            @Override
            public void flush() {
                NettyServletResponseAdapter.this.bodyData = data;
            }
        };
    }

    @Override
    public PrintWriter getWriter() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        computed(HttpHeader.CONTENT_ENCODING.asString(), charset);
    }

    @Override
    public void setContentLength(int len) {
        computed(HttpHeader.CONTENT_LENGTH.asString(), len);
    }

    @Override
    public void setContentLengthLong(long len) {
        computed(HttpHeader.CONTENT_LENGTH.asString(), len);
    }

    @Override
    public void setContentType(String type) {
        computed(HttpHeader.CONTENT_TYPE.asString(), type);
    }

    @Override
    public void setBufferSize(int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBufferSize() {
        return bodyData == null ? 0 : bodyData.length;
    }

    @Override
    public void flushBuffer() {
        this.committed = true;
    }

    @Override
    public void resetBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCommitted() {
        return this.committed;
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

    private Set<Map.Entry<String, List<Object>>> filterHeaders(Set<Map.Entry<String, List<Object>>> entrySet) {
        return entrySet.stream()
                .filter(entry -> !entry.getKey().equalsIgnoreCase(HttpHeader.LOCATION.asString()))
                .filter(entry -> !entry.getKey().equalsIgnoreCase(HttpHeader.CONTENT_LENGTH.asString()))
                .filter(entry -> !entry.getKey().equalsIgnoreCase(HttpHeader.TRANSFER_ENCODING.asString()))
                .collect(Collectors.toSet());
    }

    private void computed(String name, Object value) {
        headers.compute(name, (s, strings) -> {
            if (strings == null) {
                strings = new LinkedList<>();
            }
            if (!strings.contains(value)) {
                strings.add(value);
            }
            return strings;
        });
    }
}
