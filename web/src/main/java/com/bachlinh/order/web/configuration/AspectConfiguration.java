package com.bachlinh.order.web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.repository.monitor.RepositoryMonitor;
import com.bachlinh.order.service.monitor.ServiceMonitor;
import com.bachlinh.order.trigger.TriggerInterceptor;
import com.bachlinh.order.validator.ValidateInterceptor;

@Configuration
class AspectConfiguration {

    @Bean
    RepositoryMonitor repositoryMonitor() {
        return new RepositoryMonitor();
    }

    @Bean
    ServiceMonitor serviceMonitor() {
        return new ServiceMonitor();
    }

    @Bean
    TriggerInterceptor triggerInterceptor(EntityFactory entityFactory) {
        return new TriggerInterceptor(entityFactory);
    }

    @Bean
    ValidateInterceptor<?> validateInterceptor(EntityFactory entityFactory) {
        return new ValidateInterceptor<>(entityFactory);
    }
}