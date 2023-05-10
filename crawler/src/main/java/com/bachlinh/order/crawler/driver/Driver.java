package com.bachlinh.order.crawler.driver;

import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PrintsPage;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.virtualauthenticator.HasVirtualAuthenticator;

public interface Driver extends JavascriptExecutor, HasCapabilities, HasVirtualAuthenticator, Interactive, PrintsPage, TakesScreenshot, WebDriver {
    DriverType getType();

    String executablePath();

    void scroll(int height);
}
