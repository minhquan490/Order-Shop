package com.bachlinh.order.core.server.netty.channel.adapter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import org.eclipse.jetty.http.HttpHeader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NettyServletRequestAdapter implements HttpServletRequest {
    private static final ServerCookieDecoder COOKIE_DECODER = ServerCookieDecoder.LAX;
    private final FullHttpRequest fullHttpRequest;
    private final Map<String, Object> attributes = new HashMap<>();
    private final URI requestUri;

    private Cookie[] cachedCookie;

    private NettyServletRequestAdapter(FullHttpRequest fullHttpRequest) {
        try {
            this.fullHttpRequest = fullHttpRequest;
            this.requestUri = new URI(fullHttpRequest.uri());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Url invalid");
        }
    }

    public static HttpServletRequest from(FullHttpRequest fullHttpRequest) {
        return new NettyServletRequestAdapter(fullHttpRequest);
    }

    @Override
    public String getAuthType() {
        return HttpServletRequest.CLIENT_CERT_AUTH;
    }

    @Override
    public Cookie[] getCookies() {
        if (cachedCookie == null || cachedCookie.length == 0) {
            cachedCookie = COOKIE_DECODER.decode(fullHttpRequest.headers().getAsString(HttpHeader.COOKIE.asString()))
                    .stream()
                    .map(cookie -> {
                        var servletCookie = new Cookie(cookie.name(), cookie.value());
                        servletCookie.setPath(cookie.path());
                        servletCookie.setDomain(cookie.domain());
                        servletCookie.setHttpOnly(cookie.isHttpOnly());
                        servletCookie.setSecure(cookie.isSecure());
                        servletCookie.setMaxAge((int) cookie.maxAge());
                        return servletCookie;
                    })
                    .toList()
                    .toArray(new Cookie[0]);
        }
        return cachedCookie;
    }

    @Override
    public long getDateHeader(String name) {
        return Long.parseLong(fullHttpRequest.headers().get(HttpHeader.DATE.asString()));
    }

    @Override
    public String getHeader(String name) {
        return fullHttpRequest.headers().get(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return Collections.enumeration(fullHttpRequest.headers().getAll(name));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(fullHttpRequest.headers().names());
    }

    @Override
    public int getIntHeader(String name) {
        return fullHttpRequest.headers().getInt(name);
    }

    @Override
    public String getMethod() {
        return fullHttpRequest.method().name();
    }

    @Override
    public String getPathInfo() {
        return this.requestUri.getPath();
    }

    @Override
    public String getPathTranslated() {
        return getPathInfo().split("\\?")[0];
    }

    @Override
    public String getContextPath() {
        return "";
    }

    @Override
    public String getQueryString() {
        return this.requestUri.getQuery();
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return "";
    }

    @Override
    public String getRequestURI() {
        return getPathInfo();
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(fullHttpRequest.uri());
    }

    @Override
    public String getServletPath() {
        return "/";
    }

    @Override
    public HttpSession getSession(boolean create) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String changeSessionId() {
        return "";
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void login(String username, String password) throws ServletException {
        // Do nothing
    }

    @Override
    public void logout() {
        // Do nothing
    }

    @Override
    public Collection<Part> getParts() {
        return Collections.emptyList();
    }

    @Override
    public Part getPart(String name) {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(this.attributes.keySet());
    }

    @Override
    public String getCharacterEncoding() {
        return StandardCharsets.UTF_8.displayName();
    }

    @Override
    public void setCharacterEncoding(String env) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getContentLength() {
        return fullHttpRequest.content().array().length;
    }

    @Override
    public long getContentLengthLong() {
        return fullHttpRequest.content().array().length;
    }

    @Override
    public String getContentType() {
        return fullHttpRequest.headers().get(HttpHeader.CONTENT_TYPE.asString());
    }

    @Override
    public ServletInputStream getInputStream() {

        var arrayInput = new ByteArrayInputStream(fullHttpRequest.content().array());
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() {
                return arrayInput.read();
            }
        };
    }

    @Override
    public String getParameter(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProtocol() {
        return fullHttpRequest.protocolVersion().protocolName();
    }

    @Override
    public String getScheme() {
        return "https";
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        if (fullHttpRequest instanceof WrappedRequest wrappedRequest) {
            return wrappedRequest.getRemoteAddress().toString();
        } else {
            return null;
        }
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String name, Object o) {
        this.attributes.put(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    @Override
    public Locale getLocale() {
        return Locale.forLanguageTag(fullHttpRequest.headers().get(HttpHeader.ACCEPT_LANGUAGE.asString()));
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    @Override
    public String getRequestId() {
        return null;
    }

    @Override
    public String getProtocolRequestId() {
        return null;
    }

    @Override
    public ServletConnection getServletConnection() {
        return null;
    }
}
