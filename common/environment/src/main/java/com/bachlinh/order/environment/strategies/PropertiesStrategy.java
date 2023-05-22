package com.bachlinh.order.environment.strategies;

import com.bachlinh.order.environment.parser.ClasspathParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

public class PropertiesStrategy {
    private static final String INCLUDE_KEY = "include";
    private final ClasspathParser classpathParser;

    public PropertiesStrategy(ClasspathParser classpathParser) {
        this.classpathParser = classpathParser;
    }

    public void applyInclude(String propertiesString, Properties properties) throws IOException {
        String[] propertiesKeyPair = propertiesString.split("=");
        if (propertiesKeyPair.length != 2 || !propertiesKeyPair[0].equals(INCLUDE_KEY)) {
            return;
        }
        apply(propertiesKeyPair[1], properties);
    }

    public void apply(String path, Properties properties) throws IOException {
        String resourcePath = classpathParser.parse(path);
        URL url = new URL(resourcePath);
        try (InputStream stream = url.openStream()) {
            byte[] data = stream.readAllBytes();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] keyPair = line.split("=");
                if (keyPair[1].contains(",")) {
                    throw new IllegalStateException("Environment does not support multi value on key");
                }
                if (line.startsWith(INCLUDE_KEY)) {
                    applyInclude(line, properties);
                }
                keyPair[1] = classpathParser.parse(keyPair[1]);
                properties.setProperty(keyPair[0], keyPair[1]);
            }
        }
    }
}
