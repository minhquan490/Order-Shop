package com.bachlinh.order.crawler.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.Credentials;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chromium.ChromiumNetworkConditions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.html5.SessionStorage;
import org.openqa.selenium.logging.EventType;
import com.bachlinh.order.crawler.driver.option.ChromeOption;
import com.bachlinh.order.environment.Environment;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

class ChromeDriver implements Driver {
    private final org.openqa.selenium.chrome.ChromeDriver internalDriver;
    private final Environment environment;

    ChromeDriver(ChromeOption option, Environment crawlerEnvironment) {
        this.environment = crawlerEnvironment;
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, executablePath());
        this.internalDriver = new org.openqa.selenium.chrome.ChromeDriver(option);
    }

    @Override
    public DriverType getType() {
        return DriverType.CHROME;
    }

    @Override
    public String executablePath() {
        return environment.getProperty("driver.path");
    }

    @Override
    public void get(String url) {
        internalDriver.get(url);
    }

    @Override
    public String getCurrentUrl() {
        return internalDriver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return internalDriver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return internalDriver.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return internalDriver.findElement(by);
    }

    @Override
    public String getPageSource() {
        return internalDriver.getPageSource();
    }

    @Override
    public void close() {
        internalDriver.close();
    }

    @Override
    public void quit() {
        internalDriver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return internalDriver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return internalDriver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return internalDriver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return internalDriver.navigate();
    }

    @Override
    public Options manage() {
        return internalDriver.manage();
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
    public void setPermission(String name, String value) {
        internalDriver.setPermission(name, value);
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
}
