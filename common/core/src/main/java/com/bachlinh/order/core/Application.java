package com.bachlinh.order.core;

import org.springframework.boot.SpringApplication;
import com.bachlinh.order.core.scanner.ApplicationScanner;

public class Application {
    private Application() {
    }

    public static void run(Class<?> primarySource, String[] args) {
        new ApplicationScanner().findComponents();
        SpringApplication.run(primarySource, args);
    }
}
