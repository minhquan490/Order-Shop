package com.bachlinh.order.web.configuration;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@EnableScheduling
public class ThreadPoolConfiguration {

    @Bean
    ThreadPoolTaskExecutor threadPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setKeepAliveSeconds(60 * 60);
        threadPoolTaskExecutor.setCorePoolSize(10);
        return threadPoolTaskExecutor;
    }
}
