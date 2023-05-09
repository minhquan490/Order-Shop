package com.bachlinh.order.batch.configuration.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.bachlinh.order.batch.boot.JobCenterBooster;
import com.bachlinh.order.batch.job.JobManager;
import com.bachlinh.order.batch.job.internal.SimpleJobManagerBuilder;
import com.bachlinh.order.service.container.DependenciesResolver;

@Configuration
public class BatchBean {
    @Bean
    JobManager jobManager(DependenciesResolver resolver, @Value("${active.profile}") String profile) {
        JobCenterBooster booster = new JobCenterBooster(resolver, profile);
        JobManager.Builder builder = new SimpleJobManagerBuilder();
        return builder.dependenciesResolver(resolver)
                .profile(profile)
                .build(booster);
    }
}
