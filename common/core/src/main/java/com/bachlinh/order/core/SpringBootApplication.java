package com.bachlinh.order.core;

import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.core.container.DependenciesResolver;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;

public class SpringBootApplication {
    private SpringBootApplication() {
    }

    public static DependenciesResolver run(Class<?> primarySource, String[] args, boolean triggerEmbeddedServer) {
        new ApplicationScanner().findComponents();
        SpringApplication application = new SpringApplication(primarySource);
        if (!triggerEmbeddedServer) {
            application.setWebApplicationType(WebApplicationType.NONE);
        }
        application.setBannerMode(Banner.Mode.OFF);
        return application.run(args).getBean(DependenciesResolver.class);
    }

    public static DependenciesResolver run(Class<?> primarySource, String[] args) {
        return run(primarySource, args, false);
    }
}
