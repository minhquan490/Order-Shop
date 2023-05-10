package com.bachlinh.order.crawler.driver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.safari.SafariOptions;
import com.bachlinh.order.crawler.driver.option.ChromeOption;
import com.bachlinh.order.crawler.driver.option.EdgeOption;
import com.bachlinh.order.crawler.driver.option.FirefoxOption;
import com.bachlinh.order.crawler.loader.DriverLoader;
import com.bachlinh.order.environment.Environment;

public class SimpleDriverLoader implements DriverLoader {
    private static final String CHROME_DRIVER_NAME = "chromedriver";
    private static final String FIRE_FOX_DRIVER_NAME = "geckodriver";
    private static final String EDGE_DRIVER_NAME = "msedgedriver";
    private static final String SAFARI_DRIVER_NAME = "safaridriver";

    private final Environment environment = Environment.getInstance("crawler");

    @Override
    public Driver load(String headlessBrowserExecutablePath, Capabilities capabilities) {
        String path = environment.getProperty("driver.path");
        if (path.endsWith(CHROME_DRIVER_NAME)) {
            return new ChromeDriver(new ChromeOption(headlessBrowserExecutablePath).merge(capabilities), environment);
        }
        if (path.endsWith(FIRE_FOX_DRIVER_NAME)) {
            return new FireFoxDriver(new FirefoxOption(headlessBrowserExecutablePath).merge(capabilities), environment);
        }
        if (path.endsWith(EDGE_DRIVER_NAME)) {
            return new EdgeDriver(new EdgeOption(headlessBrowserExecutablePath).merge(capabilities), environment);
        }
        if (path.endsWith(SAFARI_DRIVER_NAME)) {
            if (capabilities == null) {
                return new SafariDriver(new SafariOptions(), environment);
            } else {
                return new SafariDriver(new SafariOptions(capabilities), environment);
            }
        }
        return null;
    }
}
