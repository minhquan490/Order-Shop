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
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.UrlEncoded;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;

public class NettyServletResponseAdapter implements HttpServletResponse, NettyHttpConvention {
    private static final ServerCookieEncoder COOKIE_ENCODER = ServerCookieEncoder.LAX;
    private final MultiValueMap<String, Object> headers = new LinkedMultiValueMap<>();
    private final ByteArrayOutputStream body = new ByteArrayOutputStream();
    private int status = 200;
    private boolean committed = false;

    private NettyServletResponseAdapter() {
    }

    public static HttpServletResponse getInstance() {
        return new NettyServletResponseAdapter();
    }

    @Override
    public FullHttpResponse toFullHttpResponse() {
        var resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status), Unpooled.copiedBuffer(body.toByteArray()));
        headers.forEach((s, objects) -> resp.headers().set(s, objects));
        return resp;
    }

    @Override
    public Http2HeadersFrame toH2HeaderFrame() {
        var defaultH2Header = new DefaultHttp2Headers();
        headers.forEach((s, objects) -> defaultH2Header.add(s.toLowerCase(), objects.stream().map(String::valueOf).toList()));
        return new DefaultHttp2HeadersFrame(defaultH2Header);
    }

    @Override
    public Http2DataFrame toH2DataFrame() {
        return new DefaultHttp2DataFrame(Unpooled.copiedBuffer(body.toByteArray()), true);
    }

    @Override
    public Http3HeadersFrame toH3HeaderFrame() {
        var defaultH3Header = new DefaultHttp3Headers();
        headers.forEach((s, objects) -> defaultH3Header.add(s, objects.stream().map(String::valueOf).toList()));
        return new DefaultHttp3HeadersFrame(defaultH3Header);
    }

    @Override
    public Http3DataFrame toH3DataFrame() {
        return new DefaultHttp3DataFrame(Unpooled.copiedBuffer(body.toByteArray()));
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
    public void sendError(int sc, String msg) throws IOException {
        status = sc;
        body.writeBytes(msg.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void sendError(int sc) throws IOException {
        status = sc;
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        status = HttpStatus.MOVED_PERMANENTLY_301;
        headers.compute(HttpHeader.LOCATION.asString(), (s, strings) -> {
            if (strings == null) {
                strings = new LinkedList<>();
            }
            strings.add(location);
            return strings;
        });
    }

    @Override
    public void setDateHeader(String name, long date) {
        headers.compute(HttpHeader.DATE.asString(), (s, strings) -> {
            if (strings == null) {
                strings = new LinkedList<>();
            }
            strings.add(date);
            return strings;
        });
    }

    @Override
    public void addDateHeader(String name, long date) {
        headers.compute(HttpHeader.DATE.asString(), (s, strings) -> {
            if (strings == null) {
                strings = new LinkedList<>();
            }
            strings.add(date);
            return strings;
        });
    }

    @Override
    public void setHeader(String name, String value) {
        headers.compute(name, (s, strings) -> {
            if (strings == null) {
                strings = new LinkedList<>();
            }
            strings.add(value);
            return strings;
        });
    }

    @Override
    public void addHeader(String name, String value) {
        headers.compute(name, (s, strings) -> {
            if (strings == null) {
                strings = new LinkedList<>();
            }
            strings.add(value);
            return strings;
        });
    }

    @Override
    public void setIntHeader(String name, int value) {
        headers.compute(name, (s, strings) -> {
            if (strings == null) {
                strings = new LinkedList<>();
            }
            strings.add(value);
            return strings;
        });
    }

    @Override
    public void addIntHeader(String name, int value) {
        headers.compute(name, (s, strings) -> {
            if (strings == null) {
                strings = new LinkedList<>();
            }
            strings.add(value);
            return strings;
        });
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
                body.write(b);
            }

            @Override
            public void flush() throws IOException {
                body.flush();
            }
        };
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        headers.compute(HttpHeader.CONTENT_ENCODING.asString(), (s, objects) -> {
            if (objects == null) {
                objects = new LinkedList<>();
            }
            objects.add(charset);
            return objects;
        });
    }

    @Override
    public void setContentLength(int len) {
        headers.compute(HttpHeader.CONTENT_LENGTH.asString(), (s, objects) -> {
            if (objects == null) {
                objects = new LinkedList<>();
            }
            objects.add(len);
            return objects;
        });
    }

    @Override
    public void setContentLengthLong(long len) {
        headers.compute(HttpHeader.CONTENT_LENGTH.asString(), (s, objects) -> {
            if (objects == null) {
                objects = new LinkedList<>();
            }
            objects.add(len);
            return objects;
        });
    }

    @Override
    public void setContentType(String type) {
        headers.compute(HttpHeader.CONTENT_TYPE.asString(), (s, objects) -> {
            if (objects == null) {
                objects = new LinkedList<>();
            }
            objects.add(type);
            return objects;
        });
    }

    @Override
    public void setBufferSize(int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBufferSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flushBuffer() throws IOException {
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
}
