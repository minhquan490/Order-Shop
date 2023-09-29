package com.bachlinh.order.web.common.interceptor;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.handler.interceptor.spi.AbstractInterceptor;
import com.bachlinh.order.utils.UnsafeUtils;

class InterceptorInitializer implements Initializer<AbstractInterceptor> {
    @Override
    public AbstractInterceptor getObject(Class<?> type, Object... params) {
        AbstractInterceptor abstractInterceptor;
        try {
            abstractInterceptor = (AbstractInterceptor) UnsafeUtils.allocateInstance(type);
        } catch (InstantiationException e) {
            return null;
        }
        return abstractInterceptor.getInstance();
    }
}
