package com.bachlinh.order.crawler.driver.option;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeOption extends ChromeOptions {
    public ChromeOption(String chromeExecutablePath) {
        setBinary(chromeExecutablePath);
    }

    @Override
    public ChromeOption merge(Capabilities extraCapabilities) {
        if (extraCapabilities != null) {
            mergeInPlace(extraCapabilities);
        }
        return this;
    }
}
