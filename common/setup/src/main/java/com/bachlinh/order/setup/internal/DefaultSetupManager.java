package com.bachlinh.order.setup.internal;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.setup.Setup;
import com.bachlinh.order.setup.SetupManager;
import com.bachlinh.order.setup.spi.AbstractSetup;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

class DefaultSetupManager implements SetupManager {

    private ContainerWrapper wrapper;
    private final String profile;
    private final Initializer<AbstractSetup> initializer = new SetupInitializer();

    DefaultSetupManager(ContainerWrapper wrapper, String profile) {
        this.wrapper = wrapper;
        this.profile = profile;
    }

    @Override
    public boolean isClose() {
        return wrapper == null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Setup> loadSetup() {
        return scan().stream()
                .filter(Setup.class::isAssignableFrom)
                .map(clazz -> newSetup((Class<Setup>) clazz))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(value -> {
                    if (value.getClass().isAnnotationPresent(Order.class)) {
                        return value.getClass().getAnnotation(Order.class).value();
                    } else {
                        return Ordered.LOWEST_PRECEDENCE;
                    }
                }))
                .toList();
    }

    @Override
    public void run() throws ClassNotFoundException {
        Collection<Setup> forSetup = loadSetup();
        forSetup.forEach(Setup::beforeExecute);
        forSetup.forEach(Setup::execute);
        forSetup.forEach(Setup::afterExecute);
    }

    @Override
    public void close() {
        wrapper = null;
    }

    private Collection<Class<?>> scan() {
        return new ApplicationScanner().findComponents();
    }

    private Setup newSetup(Class<Setup> setupClass) {
        return initializer.getObject(setupClass, wrapper, profile);
    }
}