package com.bachlinh.order.crawler.loader;

import org.openqa.selenium.Capabilities;
import com.bachlinh.order.crawler.driver.Driver;
import com.bachlinh.order.crawler.driver.SimpleDriverLoader;

public interface DriverLoader {
    Driver load(String headlessBrowserExecutablePath, Capabilities capabilities);

    static DriverLoader simpleLoader() {
        return new SimpleDriverLoader();
    }
}
