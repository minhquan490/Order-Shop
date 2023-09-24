package com.bachlinh.order.web;

import com.bachlinh.order.annotation.NettyApplication;
import com.bachlinh.order.aot.EntityManagerRuntimeHints;
import com.bachlinh.order.aot.GlobalReflectiveRuntimeHint;
import com.bachlinh.order.aot.HibernateRuntimeHints;
import com.bachlinh.order.aot.JacksonRuntimeHints;
import com.bachlinh.order.aot.WebsocketRuntimeHints;
import com.bachlinh.order.batch.configuration.spring.BatchBean;
import com.bachlinh.order.core.bean.spring.SpringContainerBean;
import com.bachlinh.order.entity.bean.spring.DataSourceBean;
import com.bachlinh.order.mail.bean.spring.EmailBean;
import com.bachlinh.order.web.configuration.AspectConfiguration;
import com.bachlinh.order.web.configuration.HttpClientConfiguration;
import com.bachlinh.order.web.configuration.SecurityConfiguration;
import com.bachlinh.order.web.configuration.ThreadPoolConfiguration;
import com.bachlinh.order.web.configuration.WebBaseConfiguration;

@NettyApplication(
        runtimeHints = {
                GlobalReflectiveRuntimeHint.class,
                JacksonRuntimeHints.class,
                HibernateRuntimeHints.class,
                EntityManagerRuntimeHints.class,
                WebsocketRuntimeHints.class
        },
        beanClasses = {
                SpringContainerBean.class,
                DataSourceBean.class,
                BatchBean.class,
                EmailBean.class,
                SecurityConfiguration.class,
                AspectConfiguration.class,
                HttpClientConfiguration.class,
                ThreadPoolConfiguration.class,
                WebBaseConfiguration.class
        },
        scanBasePackages = {
                "com.bachlinh.order.repository",
                "com.bachlinh.order.service",
                "com.bachlinh.order.web"
        }
)
public class OrderShopApplication extends com.bachlinh.order.core.NettyApplication {

    public static void main(String[] args) throws InterruptedException {
        run(OrderShopApplication.class, args);
    }
}
