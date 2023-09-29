package com.bachlinh.order.web.configuration;

import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringContainerBean {

    @Bean
    public DependenciesContainerResolver containerResolver(ApplicationContext applicationContext, @Value("${active.profile}") String profile) {
        return DependenciesContainerResolver.buildResolver(applicationContext, profile);
    }

    @Bean
    public DependenciesResolver resolver(DependenciesContainerResolver containerResolver) {
        return containerResolver.getDependenciesResolver();
    }
}
