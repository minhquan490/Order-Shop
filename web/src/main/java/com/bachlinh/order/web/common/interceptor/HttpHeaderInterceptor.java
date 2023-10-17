package com.bachlinh.order.web.common.interceptor;

import io.netty.handler.codec.http.HttpHeaderNames;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.handler.interceptor.AbstractInterceptor;

import java.text.MessageFormat;

@ActiveReflection
public class HttpHeaderInterceptor extends AbstractInterceptor {
    private Boolean enableHttp3;
    private Integer h3Port;

    private HttpHeaderInterceptor() {
    }

    @Override
    public boolean isEnable() {
        initEnableHttp3();
        return enableHttp3;
    }

    @Override
    public void postHandle(NativeRequest<?> request, NativeResponse<?> response) {
        response.addHeader(HttpHeaderNames.ALT_SVC.toString(), MessageFormat.format("h3=\":{0}\"; ma=2592000", h3Port).replace(",", ""));
    }

    @Override
    public AbstractInterceptor getInstance() {
        return new HttpHeaderInterceptor();
    }

    @Override
    public void init() {
        initEnableHttp3();
        if (h3Port == null) {
            h3Port = Integer.parseInt(getEnvironment().getProperty("server.port"));
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private void initEnableHttp3() {
        if (enableHttp3 == null) {
            enableHttp3 = Boolean.parseBoolean(getEnvironment().getProperty("server.http3.enable"));
        }
    }
}
