package com.bachlinh.order.web.configuration;

import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.core.concurrent.support.DefaultThreadPoolManager;
import com.bachlinh.order.core.concurrent.support.ThreadPoolOption;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ThreadPoolConfiguration {

    @Bean
    ThreadPoolTaskExecutor threadPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setKeepAliveSeconds(60 * 60);
        threadPoolTaskExecutor.setCorePoolSize(30);
        threadPoolTaskExecutor.setMaxPoolSize(Integer.MAX_VALUE);
        return threadPoolTaskExecutor;
    }

    @Bean
    ThreadPoolTaskScheduler scheduler(@Qualifier("threadPool") ThreadPoolTaskExecutor executor) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(3);
        scheduler.setThreadFactory(executor);
        scheduler.setThreadPriority(2);
        return scheduler;
    }

    @Bean
    ThreadPoolManager threadPoolManager() {
        ThreadPoolOption threadPoolOption = new ThreadPoolOption();
        threadPoolOption.setSchedulerPoolSize(6);
        threadPoolOption.setHttpExecutorCorePoolSize(3);
        threadPoolOption.setIndexExecutorCorePoolSize(50);
        threadPoolOption.setAsyncExecutorCorePoolSize(10);
        threadPoolOption.setServiceExecutorCorePoolSize(10);
        return new DefaultThreadPoolManager(threadPoolOption);
    }
}
