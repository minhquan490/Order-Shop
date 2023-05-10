package com.bachlinh.order.crawler.driver;

import org.openqa.selenium.Credentials;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.chromium.ChromiumNetworkConditions;
import org.openqa.selenium.chromium.HasCasting;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.chromium.HasLaunchApp;
import org.openqa.selenium.chromium.HasNetworkConditions;
import org.openqa.selenium.chromium.HasPermissions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.html5.LocationContext;
import org.openqa.selenium.html5.SessionStorage;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.logging.EventType;
import org.openqa.selenium.logging.HasLogEvents;
import org.openqa.selenium.mobile.NetworkConnection;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class ChromiumDriver extends AbstractDriver implements HasAuthentication, HasCasting, HasCdp, HasDevTools, HasLaunchApp, HasLogEvents, HasNetworkConditions, HasPermissions, LocationContext, NetworkConnection, WebStorage {
    private final org.openqa.selenium.chromium.ChromiumDriver internalDriver;

    protected ChromiumDriver(Supplier<RemoteWebDriver> driverSupplier) {
        super(driverSupplier);
        this.internalDriver = (org.openqa.selenium.chromium.ChromiumDriver) getInternalDriver();
    }

    @Override
    public void register(Predicate<URI> whenThisMatches, Supplier<Credentials> useTheseCredentials) {
        internalDriver.register(whenThisMatches, useTheseCredentials);
    }

    @Override
    public List<Map<String, String>> getCastSinks() {
        return internalDriver.getCastSinks();
    }

    @Override
    public void selectCastSink(String deviceName) {
        internalDriver.selectCastSink(deviceName);
    }

    @Override
    public void startDesktopMirroring(String deviceName) {
        internalDriver.startDesktopMirroring(deviceName);
    }

    @Override
    public void startTabMirroring(String deviceName) {
        internalDriver.startTabMirroring(deviceName);
    }

    @Override
    public String getCastIssueMessage() {
        return internalDriver.getCastIssueMessage();
    }

    @Override
    public void stopCasting(String deviceName) {
        internalDriver.stopCasting(deviceName);
    }

    @Override
    public Map<String, Object> executeCdpCommand(String commandName, Map<String, Object> parameters) {
        return internalDriver.executeCdpCommand(commandName, parameters);
    }

    @Override
    public void launchApp(String id) {
        internalDriver.launchApp(id);
    }

    @Override
    public ChromiumNetworkConditions getNetworkConditions() {
        return internalDriver.getNetworkConditions();
    }

    @Override
    public void setNetworkConditions(ChromiumNetworkConditions networkConditions) {
        internalDriver.setNetworkConditions(networkConditions);
    }

    @Override
    public void deleteNetworkConditions() {
        internalDriver.deleteNetworkConditions();
    }

    @Override
    public Optional<DevTools> maybeGetDevTools() {
        return internalDriver.maybeGetDevTools();
    }

    @Override
    public Location location() {
        return internalDriver.location();
    }

    @Override
    public void setLocation(Location location) {
        internalDriver.setLocation(location);
    }

    @Override
    public LocalStorage getLocalStorage() {
        return internalDriver.getLocalStorage();
    }

    @Override
    public SessionStorage getSessionStorage() {
        return internalDriver.getSessionStorage();
    }

    @Override
    public <X> void onLogEvent(EventType<X> kind) {
        internalDriver.onLogEvent(kind);
    }

    @Override
    public ConnectionType getNetworkConnection() {
        return internalDriver.getNetworkConnection();
    }

    @Override
    public ConnectionType setNetworkConnection(ConnectionType type) {
        return internalDriver.setNetworkConnection(type);
    }

    @Override
    public void setPermission(String name, String value) {
        internalDriver.setPermission(name, value);
    }
}
