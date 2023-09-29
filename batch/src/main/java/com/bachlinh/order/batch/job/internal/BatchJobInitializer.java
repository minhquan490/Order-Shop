package com.bachlinh.order.batch.job.internal;

import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.utils.UnsafeUtils;

public class BatchJobInitializer implements Initializer<AbstractJob> {


    @Override
    public AbstractJob getObject(Class<?> type, Object... params) {
        try {
            AbstractJob dummy = (AbstractJob) UnsafeUtils.allocateInstance(type);
            return dummy.newInstance((String) params[0], (String) params[1], (DependenciesResolver) params[2]);
        } catch (InstantiationException e) {
            throw new CriticalException("Can not allocate object [" + type.getName() + "]", e);
        }
    }
}
