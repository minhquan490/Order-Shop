package com.bachlinh.order.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import com.bachlinh.order.core.scanner.ApplicationScanner;

public class Application {
    private Application() {
    }

    public static void run(Class<?> primarySource, String[] args, boolean triggerEmbededServer) {
        new ApplicationScanner().findComponents();
        SpringApplication application = new SpringApplication(primarySource);
        if (!triggerEmbededServer) {
            application.setWebApplicationType(WebApplicationType.NONE);
        }
        application.run(args);
    }
}
