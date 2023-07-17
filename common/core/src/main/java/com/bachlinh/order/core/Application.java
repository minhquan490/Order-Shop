package com.bachlinh.order.core;

import com.bachlinh.order.core.scanner.ApplicationScanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;

public class Application {
    private Application() {
    }

    public static void run(Class<?> primarySource, String[] args, boolean triggerEmbeddedServer) {
        new ApplicationScanner().findComponents();
        SpringApplication application = new SpringApplication(primarySource);
        if (!triggerEmbeddedServer) {
            application.setWebApplicationType(WebApplicationType.NONE);
        }
        application.run(args);
    }
}
