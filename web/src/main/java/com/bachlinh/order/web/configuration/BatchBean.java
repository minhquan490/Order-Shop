package com.bachlinh.order.web.configuration;

import com.bachlinh.order.batch.boot.JobCenterBooster;
import com.bachlinh.order.batch.job.JobManager;
import com.bachlinh.order.batch.job.internal.SimpleJobManagerBuilder;
import com.bachlinh.order.core.container.DependenciesResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
