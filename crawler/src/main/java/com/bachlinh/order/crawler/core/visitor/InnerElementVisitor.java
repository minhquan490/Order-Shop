package com.bachlinh.order.crawler.core.visitor;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InnerElementVisitor {
    private final WebElement element;

    private InnerElementVisitor(WebElement element) {
        this.element = element;
    }

    public static InnerElementVisitor wrap(WebElement element) {
        return new InnerElementVisitor(element);
    }

    public String getElementTagName() {
        return element.getTagName();
    }

    public String getValue(String attributeName) {
        return element.getAttribute(attributeName);
    }

    public String getHtmlText() {
        return element.getText();
    }

    public List<WebElement> getElementByClassName(String className) {
        return element.findElements(By.className(className));
    }

    public List<WebElement> getElementByTag(String tagName) {
        return element.findElements(By.tagName(tagName));
    }

    public List<WebElement> getElementByXpath(String xpath) {
        return element.findElements(By.xpath(xpath));
    }

    public WebElement getElementById(String domElementId) {
        return element.findElement(By.id(domElementId));
    }

    @Override
    public String toString() {
        if (element instanceof RemoteWebElement castedElement) {
            return castedElement.toString();
        }
        return "";
    }

    public Map<String, Object> getJson() {
        if (element instanceof RemoteWebElement castedElement) {
            return castedElement.toJson();
        }
        return Collections.emptyMap();
    }
}
