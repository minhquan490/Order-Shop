package com.bachlinh.order.crawler.driver;

import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.WebDriver;
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

public interface Driver extends WebDriver, HasAuthentication, HasCasting, HasCdp, HasDevTools, HasLaunchApp, HasLogEvents, HasNetworkConditions, HasPermissions, LocationContext, NetworkConnection, WebStorage {
    DriverType getType();

    String executablePath();
}
