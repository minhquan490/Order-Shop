package com.bachlinh.order.environment;

import com.bachlinh.order.environment.parser.ClasspathParser;
import com.bachlinh.order.environment.strategies.PropertiesStrategy;
import com.bachlinh.order.exception.system.environment.EnvironmentException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.Map;
import java.util.Objects;
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
            throw new EnvironmentException("Can not load properties file[" + path + "]", e);
        }
        return p;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Environment other)) return false;
        final Object this$environmentName = this.environmentName;
        final Object other$environmentName = other.environmentName;
        if (!Objects.equals(this$environmentName, other$environmentName))
            return false;
        final Object this$properties = this.properties;
        final Object other$properties = other.properties;
        return Objects.equals(this$properties, other$properties);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $environmentName = this.environmentName;
        result = result * PRIME + ($environmentName == null ? 43 : $environmentName.hashCode());
        final Object $properties = this.properties;
        result = result * PRIME + ($properties == null ? 43 : $properties.hashCode());
        return result;
    }

    public String getEnvironmentName() {
        return this.environmentName;
    }
}
