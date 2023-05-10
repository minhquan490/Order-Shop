package com.bachlinh.order.crawler.driver;

import org.openqa.selenium.safari.HasDebugger;
import org.openqa.selenium.safari.HasPermissions;
import org.openqa.selenium.safari.SafariDriverService;
import org.openqa.selenium.safari.SafariOptions;
import com.bachlinh.order.environment.Environment;

import java.util.Map;

public class SafariDriver extends AbstractDriver implements Driver, HasPermissions, HasDebugger {
    private final org.openqa.selenium.safari.SafariDriver internalDriver;
    private final Environment environment;

    public SafariDriver(SafariOptions safariOptions, Environment environment) {
        super(() -> {
            System.setProperty(SafariDriverService.SAFARI_DRIVER_EXE_PROPERTY, environment.getProperty("driver.path"));
            return new org.openqa.selenium.safari.SafariDriver(safariOptions);
        });
        this.internalDriver = (org.openqa.selenium.safari.SafariDriver) getInternalDriver();
        this.environment = environment;
    }

    @Override
    public DriverType getType() {
        return DriverType.SAFARI;
    }

    @Override
    public String executablePath() {
        return environment.getProperty("driver.path");
    }

    @Override
    public void attachDebugger() {
        internalDriver.attachDebugger();
    }

    @Override
    public void setPermissions(String permission, boolean value) {
        internalDriver.setPermissions(permission, value);
    }

    @Override
    public Map<String, Boolean> getPermissions() {
        return internalDriver.getPermissions();
    }
}
