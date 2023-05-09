package com.bachlinh.order.crawler.driver.option;

import org.openqa.selenium.firefox.FirefoxOptions;

public class FirefoxOption extends FirefoxOptions {
    public FirefoxOption(String firefoxExecutablePath) {
        setBinary(firefoxExecutablePath);
    }
}
