package com.bachlinh.order.core;

import com.bachlinh.order.core.scanner.ApplicationScanner;
import org.springframework.boot.SpringApplication;

public class Application {
    private Application() {
    }

    public static void run(Class<?> primarySource, String[] args) {
        new ApplicationScanner().findComponents();
        SpringApplication.run(primarySource, args);
    }
}
