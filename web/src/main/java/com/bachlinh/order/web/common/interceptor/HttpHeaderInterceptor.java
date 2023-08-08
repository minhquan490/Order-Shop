package com.bachlinh.order.web.common.interceptor;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.handler.interceptor.spi.AbstractInterceptor;
import org.eclipse.jetty.http.HttpHeader;

import java.text.MessageFormat;

@ActiveReflection
public class HttpHeaderInterceptor extends AbstractInterceptor {
    private Boolean enableHttp3;
    private Integer h3Port;

    @ActiveReflection
    public HttpHeaderInterceptor() {
        super();
    }

    @Override
    public boolean preHandle(NativeRequest<?> request, NativeResponse<?> response) {
        if (enableHttp3 == null) {
            enableHttp3 = Boolean.parseBoolean(getEnvironment().getProperty("server.http3.enable"));
        }
        if (h3Port == null) {
            h3Port = Integer.parseInt(getEnvironment().getProperty("server.port"));
        }
        return true;
    }

    @Override
    public void postHandle(NativeRequest<?> request, NativeResponse<?> response) {
        if (enableHttp3) {
            response.addHeader(HttpHeader.ALT_SVC.asString(), MessageFormat.format("h3=\":{0}\"; ma=2592000", h3Port).replace(",", ""));
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}