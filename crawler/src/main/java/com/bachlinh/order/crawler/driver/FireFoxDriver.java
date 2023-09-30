package com.bachlinh.order.crawler.driver;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.bidi.BiDi;
import org.openqa.selenium.bidi.HasBiDi;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.firefox.FirefoxCommandContext;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.firefox.HasContext;
import org.openqa.selenium.firefox.HasExtensions;
import org.openqa.selenium.firefox.HasFullPageScreenshot;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.SessionStorage;
import org.openqa.selenium.html5.WebStorage;
import com.bachlinh.order.crawler.driver.option.FirefoxOption;
import com.bachlinh.order.core.environment.Environment;

import java.nio.file.Path;
import java.util.Optional;

class FireFoxDriver extends AbstractDriver implements Driver, WebStorage, HasExtensions, HasFullPageScreenshot, HasContext, HasDevTools, HasBiDi {
    private final FirefoxDriver internalDriver;
    private final Environment environment;

    FireFoxDriver(FirefoxOption firefoxOption, Environment environment) {
        super(() -> {
            System.setProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY, environment.getProperty("driver.path"));
            return new FirefoxDriver(firefoxOption);
        });
        this.internalDriver = (FirefoxDriver) getInternalDriver();
        this.environment = environment;
    }

    @Override
    public DriverType getType() {
        return DriverType.FIRE_FOX;
    }

    @Override
    public String executablePath() {
        return environment.getProperty("driver.path");
    }

    @Override
    public Optional<BiDi> maybeGetBiDi() {
        return internalDriver.maybeGetBiDi();
    }

    /**
     * @deprecated Use W3C-compliant BiDi protocol. Use {{@link #maybeGetBiDi()}}
     */
    @Override
    @Deprecated(forRemoval = true)
    public Optional<DevTools> maybeGetDevTools() {
        return internalDriver.maybeGetDevTools();
    }

    @Override
    public void setContext(FirefoxCommandContext context) {
        internalDriver.setContext(context);
    }

    @Override
    public FirefoxCommandContext getContext() {
        return internalDriver.getContext();
    }

    @Override
    public String installExtension(Path path) {
        return internalDriver.installExtension(path);
    }

    @Override
    public String installExtension(Path path, Boolean temporary) {
        return internalDriver.installExtension(path, temporary);
    }

    @Override
    public void uninstallExtension(String extensionId) {
        internalDriver.uninstallExtension(extensionId);
    }

    @Override
    public <X> X getFullPageScreenshotAs(OutputType<X> outputType) {
        return internalDriver.getFullPageScreenshotAs(outputType);
    }

    @Override
    public LocalStorage getLocalStorage() {
        return internalDriver.getLocalStorage();
    }

    @Override
    public SessionStorage getSessionStorage() {
        return internalDriver.getSessionStorage();
    }
}
