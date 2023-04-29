package com.bachlinh.order.entity.setup.internal;

import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.entity.setup.spi.Setup;
import com.bachlinh.order.entity.setup.spi.SetupManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

class DefaultSetupManager implements SetupManager {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(DefaultSetupManager.class);
    private ApplicationContext applicationContext;

    DefaultSetupManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean isClose() {
        return applicationContext == null;
    }

    @Override
    public Collection<Setup> loadSetup() throws ClassNotFoundException {
        return scan().stream()
                .filter(Setup.class::isAssignableFrom)
                .filter(clazz -> clazz.isAnnotationPresent(Order.class))
                .map(clazz -> {
                    try {
                        Constructor<?> constructor = clazz.getDeclaredConstructor(ApplicationContext.class);
                        if (!Modifier.isPrivate(constructor.getModifiers())) {
                            constructor.setAccessible(true);
                        }
                        Setup result = (Setup) constructor.newInstance(applicationContext);
                        log.info("Create instance for class [{}] done", clazz.getName());
                        return result;
                    } catch (Exception e) {
                        log.error("Create instance for class [{}] failure", clazz.getName(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(value -> value.getClass().getAnnotation(Order.class).value()))
                .toList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void run() throws ClassNotFoundException {
        Collection<Setup> forSetup = loadSetup();
        forSetup.forEach(Setup::beforeExecute);
        forSetup.forEach(Setup::execute);
        forSetup.forEach(Setup::afterExecute);
    }

    @Override
    public void close() {
        applicationContext = null;
    }

    private Collection<Class<?>> scan() {
        return new ApplicationScanner().findComponents();
    }
}