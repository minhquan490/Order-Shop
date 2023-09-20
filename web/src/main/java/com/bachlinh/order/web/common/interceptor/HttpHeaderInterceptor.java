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

    private HttpHeaderInterceptor() {
    }

    @Override
    public void postHandle(NativeRequest<?> request, NativeResponse<?> response) {
        boolean isEnableHttp3 = enableHttp3;
        if (isEnableHttp3) {
            response.addHeader(HttpHeader.ALT_SVC.asString(), MessageFormat.format("h3=\":{0}\"; ma=2592000", h3Port).replace(",", ""));
        }
    }

    @Override
    public AbstractInterceptor getInstance() {
        return new HttpHeaderInterceptor();
    }

    @Override
    public void init() {
        if (enableHttp3 == null) {
            enableHttp3 = Boolean.parseBoolean(getEnvironment().getProperty("server.http3.enable"));
        }
        if (h3Port == null) {
            h3Port = Integer.parseInt(getEnvironment().getProperty("server.port"));
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
