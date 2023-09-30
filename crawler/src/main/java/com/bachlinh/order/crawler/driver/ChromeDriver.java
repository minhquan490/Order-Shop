package com.bachlinh.order.crawler.driver;

import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chromium.HasCasting;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.chromium.HasLaunchApp;
import org.openqa.selenium.chromium.HasNetworkConditions;
import org.openqa.selenium.chromium.HasPermissions;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.html5.LocationContext;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.logging.HasLogEvents;
import org.openqa.selenium.mobile.NetworkConnection;
import com.bachlinh.order.crawler.driver.option.ChromeOption;
import com.bachlinh.order.core.environment.Environment;

class ChromeDriver extends ChromiumDriver implements Driver, HasAuthentication, HasCasting, HasCdp, HasDevTools, HasLaunchApp, HasLogEvents, HasNetworkConditions, HasPermissions, LocationContext, NetworkConnection, WebStorage {
    private final Environment environment;

    ChromeDriver(ChromeOption option, Environment crawlerEnvironment) {
        super(() -> {
            System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, crawlerEnvironment.getProperty("driver.path"));
            return new org.openqa.selenium.chrome.ChromeDriver(option);
        });
        this.environment = crawlerEnvironment;
    }

    @Override
    public DriverType getType() {
        return DriverType.CHROME;
    }

    @Override
    public String executablePath() {
        return environment.getProperty("driver.path");
    }
}
