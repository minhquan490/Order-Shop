package com.bachlinh.order.crawler.driver.option;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.edge.EdgeOptions;

public class EdgeOption extends EdgeOptions {
    public EdgeOption(String edgeExecutablePath) {
        setBinary(edgeExecutablePath);
    }

    @Override
    public EdgeOption merge(Capabilities extraCapabilities) {
        if (extraCapabilities != null) {
            mergeInPlace(extraCapabilities);
        }
        return this;
    }
}
