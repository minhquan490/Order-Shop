package com.bachlinh.order.setup.internal;

import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.entity.Setup;
import com.bachlinh.order.entity.SetupManager;
import com.bachlinh.order.service.container.ContainerWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

class DefaultSetupManager implements SetupManager {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(DefaultSetupManager.class);
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
    public Collection<Setup> loadSetup() throws ClassNotFoundException {
        return scan().stream()
                .filter(Setup.class::isAssignableFrom)
                .filter(clazz -> clazz.isAnnotationPresent(Order.class))
                .map(clazz -> (Setup) newInstance(clazz, new Class[]{ContainerWrapper.class, String.class}, new Object[]{wrapper, profile}))
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
        wrapper = null;
    }

    private Collection<Class<?>> scan() {
        return new ApplicationScanner().findComponents();
    }

    private Object newInstance(Class<?> clazz, Class<?>[] paramTypes, Object[] param) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(paramTypes);
            if (!Modifier.isPrivate(constructor.getModifiers())) {
                constructor.setAccessible(true);
            }
            Object result = constructor.newInstance(param);
            if (log.isDebugEnabled()) {
                log.debug("Init instance for class [{}] complete", clazz.getName());
            }
            return result;
        } catch (Exception e) {
            log.warn("Init instance for class [{}] failure, skip it !", clazz.getName());
            return null;
        }
    }
}