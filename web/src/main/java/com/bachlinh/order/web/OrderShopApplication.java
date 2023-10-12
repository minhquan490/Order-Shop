package com.bachlinh.order.web;

import com.bachlinh.order.aot.EntityManagerRuntimeHints;
import com.bachlinh.order.aot.GlobalReflectiveRuntimeHint;
import com.bachlinh.order.aot.HibernateRuntimeHints;
import com.bachlinh.order.aot.JacksonRuntimeHints;
import com.bachlinh.order.aot.WebsocketRuntimeHints;
import com.bachlinh.order.core.annotation.NettyApplication;
import com.bachlinh.order.web.configuration.BeanDeclaration;

@NettyApplication(
        runtimeHints = {
                GlobalReflectiveRuntimeHint.class,
                JacksonRuntimeHints.class,
                HibernateRuntimeHints.class,
                EntityManagerRuntimeHints.class,
                WebsocketRuntimeHints.class
        },
        beanClasses = BeanDeclaration.class
)
public class OrderShopApplication extends com.bachlinh.order.core.NettyApplication {

    public static void main(String[] args) throws InterruptedException {
        run(OrderShopApplication.class, args);
    }
}
