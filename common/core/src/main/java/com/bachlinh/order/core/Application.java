package com.bachlinh.order.core;

import org.springframework.boot.SpringApplication;

public class Application {
    private Application() {
    }

    public static void run(Class<?> primarySource, String[] args) {
        SpringApplication.run(primarySource, args);
    }
}
