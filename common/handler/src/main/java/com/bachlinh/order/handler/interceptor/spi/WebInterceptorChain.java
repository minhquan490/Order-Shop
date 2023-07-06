package com.bachlinh.order.handler.interceptor.spi;

public interface WebInterceptorChain extends ObjectInterceptor {

    void registerInterceptor(WebInterceptor interceptor);
}
