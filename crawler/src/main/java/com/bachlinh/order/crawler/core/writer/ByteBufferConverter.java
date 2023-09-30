package com.bachlinh.order.crawler.core.writer;

import com.bachlinh.order.core.http.converter.spi.Converter;
import com.bachlinh.order.crawler.core.visitor.InnerElementVisitor;
import com.bachlinh.order.core.utils.JacksonUtils;
import org.openqa.selenium.WebElement;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class ByteBufferConverter implements Converter<ByteBuffer, InnerElementVisitor> {

    @Override
    public ByteBuffer convert(InnerElementVisitor message) {
        Map<String, Object> json = message.getJson();
        byte[] data;
        if (json.isEmpty()) {
            Element element = new Element(message);
            data = JacksonUtils.writeObjectAsBytes(element);
        } else {
            data = JacksonUtils.writeObjectAsBytes(json);
        }
        return ByteBuffer.wrap(data);
    }

    private static class Element {
        private final String tagName;
        private String value;
        private final String htmlText;
        private final Element[] children;

        Element(InnerElementVisitor visitor) {
            tagName = visitor.getElementTagName();
            value = visitor.getValue("oldValue");
            if (value == null) {
                value = visitor.getValue("src");
            }
            htmlText = visitor.getHtmlText();
            children = findChildren(visitor, new LinkedList<>()).toArray(new Element[0]);
        }

        public Element[] getChildren() {
            return children;
        }

        public String getHtmlText() {
            return htmlText;
        }

        public String getTagName() {
            return tagName;
        }

        public String getValue() {
            return value;
        }

        private Collection<Element> findChildren(InnerElementVisitor element, Collection<Element> store) {
            List<WebElement> webElements = element.getElementByXpath(".//*");
            for (WebElement webElement : webElements) {
                store.add(new Element(InnerElementVisitor.wrap(webElement)));
                findChildren(InnerElementVisitor.wrap(webElement), store);
            }
            return store;
        }
    }
}
