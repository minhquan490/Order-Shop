package com.bachlinh.order.environment.parser;

import org.springframework.core.io.ResourceLoader;
import static com.bachlinh.order.environment.Environment.CLASSPATH;
import static com.bachlinh.order.environment.Environment.PREFIX;
import static com.bachlinh.order.environment.Environment.SUFFIX;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

public class ClasspathParser {
    private final ResourceLoader loader;

    public ClasspathParser(ResourceLoader loader) {
        this.loader = loader;
    }

    public String parse(String target) throws IOException {
        if (target.startsWith(CLASSPATH)) {
            String actualPropertiesFileName = target.replace(CLASSPATH, PREFIX).concat(SUFFIX);
            URL u = loader.getResource(MessageFormat.format(CLASSPATH + "{0}", actualPropertiesFileName)).getURL();
            return u.toString();
        } else {
            return target;
        }
    }
}
