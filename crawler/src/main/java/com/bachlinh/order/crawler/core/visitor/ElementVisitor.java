package com.bachlinh.order.crawler.core.visitor;

import org.openqa.selenium.WebElement;

public class ElementVisitor {
    private final WebElement webElement;

    private ElementVisitor(WebElement element) {
        this.webElement = element;
    }

    public static ElementVisitor visit(WebElement element) {
        return new ElementVisitor(element);
    }

    public int getHeight() {
        return webElement.getSize().getHeight();
    }

    public String getTagName() {
        return webElement.getTagName();
    }

    public String getHtmlText() {
        return webElement.getText();
    }

    public String getValue(String attributeName) {
        return webElement.getAttribute(attributeName);
    }
}
