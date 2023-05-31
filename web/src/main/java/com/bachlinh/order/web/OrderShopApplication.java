package com.bachlinh.order.web;

import com.bachlinh.order.annotation.SpringApplication;
import com.bachlinh.order.aot.GlobalReflectiveRuntimeHint;
import com.bachlinh.order.batch.configuration.spring.BatchBean;
import com.bachlinh.order.core.Application;
import com.bachlinh.order.core.bean.spring.SpringContainerBean;
import com.bachlinh.order.entity.bean.spring.CacheSourceBean;
import com.bachlinh.order.entity.bean.spring.DataSourceBean;
import com.bachlinh.order.mail.bean.spring.EmailBean;

@SpringApplication(
        runtimeHints = GlobalReflectiveRuntimeHint.class,
        beanClasses = {SpringContainerBean.class, DataSourceBean.class, CacheSourceBean.class, BatchBean.class, EmailBean.class},
        scanBasePackages = {"com.bachlinh.order.repository", "com.bachlinh.order.service", "com.bachlinh.order.web"}
)
public class OrderShopApplication {

    public static void main(String[] args) {
        Application.run(OrderShopApplication.class, args, true);
    }
}
