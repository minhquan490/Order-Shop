package com.bachlinh.order.crawler.driver.option;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;

public class FirefoxOption extends FirefoxOptions {
    public FirefoxOption(String firefoxExecutablePath) {
        setBinary(firefoxExecutablePath);
    }

    private FirefoxOption(File binary, Capabilities capabilities) {
        super(capabilities);
        setBinary(binary.toPath());
    }

    @Override
    public FirefoxOption merge(Capabilities capabilities) {
        if (capabilities != null) {
            return new FirefoxOption(getBinary().getFile(), capabilities);
        }
        return this;
    }
}
