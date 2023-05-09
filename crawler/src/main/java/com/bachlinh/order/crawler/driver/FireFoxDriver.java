package com.bachlinh.order.crawler.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.Credentials;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chromium.ChromiumNetworkConditions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.html5.SessionStorage;
import org.openqa.selenium.logging.EventType;
import com.bachlinh.order.crawler.driver.option.FirefoxOption;
import com.bachlinh.order.environment.Environment;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

class FireFoxDriver implements Driver {
    private final FirefoxDriver internalDriver;
    private final Environment environment;

    FireFoxDriver(FirefoxOption firefoxOption, Environment environment) {
        this.internalDriver = new FirefoxDriver(firefoxOption);
        this.environment = environment;
    }

    @Override
    public DriverType getType() {
        return null;
    }

    @Override
    public String executablePath() {
        return null;
    }

    @Override
    public void register(Predicate<URI> whenThisMatches, Supplier<Credentials> useTheseCredentials) {

    }

    @Override
    public void get(String url) {

    }

    @Override
    public String getCurrentUrl() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public List<WebElement> findElements(By by) {
        return null;
    }

    @Override
    public WebElement findElement(By by) {
        return null;
    }

    @Override
    public String getPageSource() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void quit() {

    }

    @Override
    public Set<String> getWindowHandles() {
        return null;
    }

    @Override
    public String getWindowHandle() {
        return null;
    }

    @Override
    public TargetLocator switchTo() {
        return null;
    }

    @Override
    public Navigation navigate() {
        return null;
    }

    @Override
    public Options manage() {
        return null;
    }

    @Override
    public List<Map<String, String>> getCastSinks() {
        return null;
    }

    @Override
    public void selectCastSink(String deviceName) {

    }

    @Override
    public void startDesktopMirroring(String deviceName) {

    }

    @Override
    public void startTabMirroring(String deviceName) {

    }

    @Override
    public String getCastIssueMessage() {
        return null;
    }

    @Override
    public void stopCasting(String deviceName) {

    }

    @Override
    public Map<String, Object> executeCdpCommand(String commandName, Map<String, Object> parameters) {
        return null;
    }

    @Override
    public void launchApp(String id) {

    }

    @Override
    public ChromiumNetworkConditions getNetworkConditions() {
        return null;
    }

    @Override
    public void setNetworkConditions(ChromiumNetworkConditions networkConditions) {

    }

    @Override
    public void deleteNetworkConditions() {

    }

    @Override
    public void setPermission(String name, String value) {

    }

    @Override
    public Optional<DevTools> maybeGetDevTools() {
        return Optional.empty();
    }

    @Override
    public Location location() {
        return null;
    }

    @Override
    public void setLocation(Location location) {

    }

    @Override
    public LocalStorage getLocalStorage() {
        return null;
    }

    @Override
    public SessionStorage getSessionStorage() {
        return null;
    }

    @Override
    public <X> void onLogEvent(EventType<X> kind) {

    }

    @Override
    public ConnectionType getNetworkConnection() {
        return null;
    }

    @Override
    public ConnectionType setNetworkConnection(ConnectionType type) {
        return null;
    }
}
