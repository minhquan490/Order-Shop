package com.bachlinh.order.web.configuration;

import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.repository.monitor.RepositoryMonitor;
import com.bachlinh.order.service.monitor.ServiceMonitor;
import com.bachlinh.order.validate.validator.ValidateInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AspectConfiguration {

    @Bean
    RepositoryMonitor repositoryMonitor() {
        return new RepositoryMonitor();
    }

    @Bean
    ServiceMonitor serviceMonitor() {
        return new ServiceMonitor();
    }

    @Bean
    ValidateInterceptor<?> validateInterceptor(EntityFactory entityFactory) {
        return new ValidateInterceptor<>(entityFactory);
    }
}
