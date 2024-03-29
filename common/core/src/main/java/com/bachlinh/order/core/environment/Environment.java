package com.bachlinh.order.core.environment;

import com.bachlinh.order.core.environment.parser.ClasspathParser;
import com.bachlinh.order.core.environment.strategies.PropertiesStrategy;
import com.bachlinh.order.core.exception.system.environment.EnvironmentException;
import com.google.common.base.Objects;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The system environment object, multiple environment can be store and use in runtime.
 * When environment is requested, this object will look for the properties file to setting
 * environment. Properties file format application-${environment name}.
 *
 * @author Hoang Minh Quan
 */
public final class Environment {
    public static final String PREFIX = "config/application-";
    public static final String SUFFIX = ".properties";
    public static final String CLASSPATH = "classpath:";
    public static final String INCLUDE = "include";
    public static final String CLASSPATH_CONFIG = "classpathConfig:";
    public static final ResourceLoader SINGLETON_LOADER = new DefaultResourceLoader();
    private static final Map<String, Environment> environments = new ConcurrentHashMap<>();
    private static final PropertiesStrategy STRATEGY = new PropertiesStrategy(new ClasspathParser(SINGLETON_LOADER));
    private static String mainEnvironmentName = null;

    private final String environmentName;
    private final Properties properties;

    private Environment(String name) {
        if (name.contains("/")) {
            this.environmentName = name.substring(name.lastIndexOf("/"));
        } else {
            this.environmentName = name;
        }
        String path = name.replace(environmentName, PREFIX.concat(environmentName).concat(SUFFIX));
        this.properties = loadProperties(path);
    }

    /**
     * Create environment object with given name and cache it for improved performance.
     * Call this method will cause a problem when it can not find properties file in classpath.
     *
     * @return Environment object.
     */
    public static Environment getInstance(String name) {
        if (name == null) {
            return null;
        }
        if (mainEnvironmentName == null && (name.equals("local") || name.equals("prod"))) {
            mainEnvironmentName = name;
        }
        environments.computeIfAbsent(name, s -> new Environment(name));
        return environments.get(name);
    }

    public static String getMainEnvironmentName() {
        return Environment.mainEnvironmentName;
    }

    /**
     * Return system properties with given name or null if name is not existed in environment.
     *
     * @param propertyName Requested properties name.
     * @return System properties value or null if name is not existed.
     */
    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName, propertyName);
    }

    /**
     * Load system properties from properties file system.
     *
     * @param path Path of system environment properties.
     * @return Properties of system environment.
     */
    private Properties loadProperties(String path) {
        Properties p = new Properties();
        try {
            path = SINGLETON_LOADER.getResource(CLASSPATH + path).getURL().toString();
            STRATEGY.apply(path, p);
        } catch (Exception e) {
            String message = STR. "Can not load properties file [\{ path }]" ;
            throw new EnvironmentException(message, e);
        }
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Environment that)) return false;
        return Objects.equal(getEnvironmentName(), that.getEnvironmentName()) && Objects.equal(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getEnvironmentName(), properties);
    }

    public String getEnvironmentName() {
        return this.environmentName;
    }
}
