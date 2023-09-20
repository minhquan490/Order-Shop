package com.bachlinh.order.web.configuration;

import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.core.concurrent.ThreadPoolOption;
import com.bachlinh.order.core.concurrent.ThreadPoolOptionHolder;
import com.bachlinh.order.core.concurrent.support.DefaultThreadPoolManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ThreadPoolConfiguration {

    @Bean
    ThreadPoolTaskExecutor threadPool(ThreadPoolOptionHolder threadPoolOptionHolder) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setKeepAliveSeconds(60 * 60);
        threadPoolTaskExecutor.setCorePoolSize(30);
        threadPoolTaskExecutor.setMaxPoolSize(Integer.MAX_VALUE);
        threadPoolTaskExecutor.setThreadFactory(threadPoolOptionHolder.getThreadOption().getVirtualThreadFactory());
        return threadPoolTaskExecutor;
    }

    @Bean
    ThreadPoolTaskScheduler scheduler(ThreadPoolOptionHolder threadPoolOptionHolder) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(3);
        scheduler.setThreadFactory(threadPoolOptionHolder.getThreadOption().getVirtualThreadFactory());
        scheduler.setThreadPriority(2);
        return scheduler;
    }

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
