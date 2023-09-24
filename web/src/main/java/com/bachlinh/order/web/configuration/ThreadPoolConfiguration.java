package com.bachlinh.order.web.configuration;

import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.core.concurrent.ThreadPoolOption;
import com.bachlinh.order.core.concurrent.ThreadPoolOptionHolder;
import com.bachlinh.order.core.concurrent.support.DefaultThreadPoolManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ThreadPoolConfiguration {

    @Bean
    @Primary
    ThreadPoolManager threadPoolManager() {
        ThreadPoolOption threadPoolOption = new ThreadPoolOption();
        threadPoolOption.setSchedulerPoolSize(6);
        threadPoolOption.setHttpExecutorCorePoolSize(3);
        threadPoolOption.setIndexExecutorCorePoolSize(50);
        threadPoolOption.setAsyncExecutorCorePoolSize(10);
        threadPoolOption.setServiceExecutorCorePoolSize(10);
        return new DefaultThreadPoolManager(threadPoolOption);
    }

    @Bean
    ThreadPoolOptionHolder threadPoolOptionHolder(ThreadPoolManager threadPoolManager) {
        return (ThreadPoolOptionHolder) threadPoolManager;
    }
}
