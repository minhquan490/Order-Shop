package com.bachlinh.order.crawler.driver;

public interface DriverLoader {
    Driver load(String headlessBrowserExecutablePath);

    static DriverLoader simpleLoader() {
        return new SimpleDriverLoader();
    }
}
