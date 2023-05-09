package com.bachlinh.order.crawler.driver;

import com.bachlinh.order.crawler.driver.option.ChromeOption;
import com.bachlinh.order.crawler.driver.option.FirefoxOption;
import com.bachlinh.order.environment.Environment;

class SimpleDriverLoader implements DriverLoader {
    private static final String CHROME_DRIVER_NAME = "chromedriver";
    private static final String FIRE_FOX_DRIVER_NAME = "geckodriver";
    private static final String EDGE_DRIVER_NAME = "msedgedriver";
    private static final String SAFARI_DRIVER_NAME = "safaridriver";

    private final Environment environment = Environment.getInstance("crawler");

    @Override
    public Driver load(String headlessBrowserExecutablePath) {
        String path = environment.getProperty("driver.path");
        if (path.endsWith(CHROME_DRIVER_NAME)) {
            return new ChromeDriver(new ChromeOption(headlessBrowserExecutablePath), environment);
        }
        if (path.endsWith(FIRE_FOX_DRIVER_NAME)) {
            return new FireFoxDriver(new FirefoxOption(headlessBrowserExecutablePath), environment);
        }
        return null;
    }
}
