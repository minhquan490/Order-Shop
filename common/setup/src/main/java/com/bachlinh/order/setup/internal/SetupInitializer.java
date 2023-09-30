package com.bachlinh.order.setup.internal;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.setup.spi.AbstractSetup;
import com.bachlinh.order.core.utils.UnsafeUtils;

class SetupInitializer implements Initializer<AbstractSetup> {
    @Override
    public AbstractSetup getObject(Class<?> type, Object... params) {
        AbstractSetup setup;
        try {
            setup = (AbstractSetup) UnsafeUtils.allocateInstance(type);
        } catch (InstantiationException e) {
            throw new CriticalException(e);
        }
        return (AbstractSetup) setup.newInstance((ContainerWrapper) params[0], (String) params[1]);
    }
}
