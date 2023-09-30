package com.bachlinh.order.crawler.driver;

import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.chromium.HasCasting;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.chromium.HasLaunchApp;
import org.openqa.selenium.chromium.HasNetworkConditions;
import org.openqa.selenium.chromium.HasPermissions;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.html5.LocationContext;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.logging.HasLogEvents;
import org.openqa.selenium.mobile.NetworkConnection;
import com.bachlinh.order.crawler.driver.option.EdgeOption;
import com.bachlinh.order.core.environment.Environment;

class EdgeDriver extends ChromiumDriver implements Driver, HasAuthentication, HasCasting, HasCdp, HasDevTools, HasLaunchApp, HasLogEvents, HasNetworkConditions, HasPermissions, LocationContext, NetworkConnection, WebStorage {
    private final Environment environment;

    public EdgeDriver(EdgeOption option, Environment environment) {
        super(() -> {
            System.setProperty(EdgeDriverService.EDGE_DRIVER_EXE_PROPERTY, environment.getProperty("driver.path"));
            return new org.openqa.selenium.edge.EdgeDriver(option);
        });
        this.environment = environment;
    }

    @Override
    public DriverType getType() {
        return DriverType.EDGE;
    }

    @Override
    public String executablePath() {
        return environment.getProperty("driver.path");
    }
}
