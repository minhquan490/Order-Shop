package com.bachlinh.order.crawler.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Pdf;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public abstract class AbstractDriver implements Driver {
    private final RemoteWebDriver internalDriver;

    protected AbstractDriver(Supplier<RemoteWebDriver> driverSupplier) {
        this.internalDriver = driverSupplier.get();
    }

    @Override
    public Capabilities getCapabilities() {
        return internalDriver.getCapabilities();
    }

    @Override
    public Object executeScript(String script, Object... args) {
        return internalDriver.executeScript(script, args);
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return internalDriver.executeAsyncScript(script, args);
    }

    @Override
    public Pdf print(PrintOptions printOptions) throws WebDriverException {
        return internalDriver.print(printOptions);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return internalDriver.getScreenshotAs(target);
    }

    @Override
    public void perform(Collection<Sequence> actions) {
        internalDriver.perform(actions);
    }

    @Override
    public void resetInputState() {
        internalDriver.resetInputState();
    }

    @Override
    public VirtualAuthenticator addVirtualAuthenticator(VirtualAuthenticatorOptions options) {
        return internalDriver.addVirtualAuthenticator(options);
    }

    @Override
    public void removeVirtualAuthenticator(VirtualAuthenticator authenticator) {
        internalDriver.removeVirtualAuthenticator(authenticator);
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
    public void scroll(int height) {
        internalDriver.manage().window().setPosition(new Point(0, height));
    }

    protected RemoteWebDriver getInternalDriver() {
        return internalDriver;
    }
}
