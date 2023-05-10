package com.bachlinh.order.crawler.core.visitor;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.bachlinh.order.crawler.driver.Driver;

import java.util.List;

public class PageVisitor {
    private final Driver driver;
    private final String url;

    public PageVisitor(Driver driver, String url) {
        this.url = url;
        this.driver = driver;
    }

    public void visit() {
        driver.get(url);
    }

    public List<InnerElementVisitor> getElementByClassName(String classList) {
        List<WebElement> elements;
        while (true) {
            elements = driver.findElements(By.className(classList));
            if (elements.size() >= 14) {
                break;
            }
            driver.scroll(500);
        }
        return elements.subList(3, elements.size()).stream().map(InnerElementVisitor::wrap).toList();
    }
}
