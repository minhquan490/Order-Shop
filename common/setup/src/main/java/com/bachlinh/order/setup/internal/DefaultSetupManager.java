package com.bachlinh.order.setup.internal;

import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.entity.Setup;
import com.bachlinh.order.entity.SetupManager;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.setup.spi.AbstractSetup;
import com.bachlinh.order.utils.UnsafeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

class DefaultSetupManager implements SetupManager {

    private ContainerWrapper wrapper;
    private final String profile;

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
        AbstractSetup setup;
        try {
            setup = (AbstractSetup) UnsafeUtils.allocateInstance(setupClass);
        } catch (InstantiationException e) {
            throw new CriticalException(e);
        }
        return setup.newInstance(wrapper, profile);
    }
}