package com.bachlinh.order.web.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import com.bachlinh.order.batch.boot.JobCenterBooster;
import com.bachlinh.order.batch.job.JobManager;
import com.bachlinh.order.batch.job.internal.SimpleJobManagerBuilder;
import com.bachlinh.order.service.container.DependenciesResolver;

@Configuration
@EnableAsync(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@EnableScheduling
public class ThreadPoolConfiguration {

    @Bean
    ThreadPoolTaskExecutor threadPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setKeepAliveSeconds(60 * 60);
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setMaxPoolSize(Integer.MAX_VALUE);
        return threadPoolTaskExecutor;
    }

    @Bean
    ThreadPoolTaskScheduler scheduler(ThreadPoolTaskExecutor executor) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(3);
        scheduler.setThreadFactory(executor);
        scheduler.setThreadPriority(2);
        return scheduler;
    }

    @Bean
    JobManager jobManager(DependenciesResolver resolver, @Value("${active.profile}") String profile) {
        JobCenterBooster booster = new JobCenterBooster(resolver, profile);
        JobManager.Builder builder = new SimpleJobManagerBuilder();
        return builder.dependenciesResolver(resolver)
                .profile(profile)
                .build(booster);
    }
}
