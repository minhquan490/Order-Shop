package com.bachlinh.order.environment.parser;

import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import static com.bachlinh.order.environment.Environment.CLASSPATH;
import static com.bachlinh.order.environment.Environment.CLASSPATH_CONFIG;
import static com.bachlinh.order.environment.Environment.PREFIX;

public class ClasspathParser {
    private final ResourceLoader loader;

    public ClasspathParser(ResourceLoader loader) {
        this.loader = loader;
    }

    public String parse(String target) throws IOException {
        if (target.startsWith(CLASSPATH)) {
            URL u = loader.getResource(target).getURL();
            return u.toString();
        } else if (target.startsWith(CLASSPATH_CONFIG)) {
            String actualPropertiesFileName = target.replace(CLASSPATH_CONFIG, PREFIX);
            URL u = loader.getResource(MessageFormat.format(CLASSPATH + "{0}", actualPropertiesFileName)).getURL();
            return u.toString();
        } else {
            return target;
        }
    }
}
