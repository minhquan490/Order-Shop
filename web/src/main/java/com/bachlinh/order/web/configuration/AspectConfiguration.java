package com.bachlinh.order.web.configuration;

import com.bachlinh.order.repository.monitor.RepositoryMonitor;
import com.bachlinh.order.service.monitor.ServiceMonitor;
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
}
