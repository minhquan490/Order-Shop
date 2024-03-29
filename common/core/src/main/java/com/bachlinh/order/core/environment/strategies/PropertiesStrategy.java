package com.bachlinh.order.core.environment.strategies;

import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.environment.parser.ClasspathParser;
import com.bachlinh.order.core.exception.system.common.CriticalException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class PropertiesStrategy {
    private final ClasspathParser classpathParser;

    public PropertiesStrategy(ClasspathParser classpathParser) {
        this.classpathParser = classpathParser;
    }

    public void applyInclude(String propertiesString, Properties properties) throws IOException {
        String[] propertiesKeyPair = propertiesString.split("=");
        if (propertiesKeyPair.length != 2 || !propertiesKeyPair[0].equals(Environment.INCLUDE)) {
            return;
        }
        apply(propertiesKeyPair[1], properties);
    }

    public void apply(String path, Properties properties) throws IOException {
        try {
            String resourcePath = classpathParser.parse(path);
            URL url = new URI(resourcePath).toURL();
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
                    if (line.startsWith(Environment.INCLUDE)) {
                        applyInclude(line.concat(Environment.SUFFIX), properties);
                        continue;
                    }
                    keyPair[1] = classpathParser.parse(keyPair[1]);
                    properties.setProperty(keyPair[0], keyPair[1]);
                }
            }
        } catch (URISyntaxException e) {
            throw new CriticalException(e);
        }
    }
}
