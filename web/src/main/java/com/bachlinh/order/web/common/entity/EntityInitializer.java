package com.bachlinh.order.web.common.entity;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.utils.UnsafeUtils;

class EntityInitializer implements Initializer<BaseEntity<?>> {
    @Override
    public BaseEntity<?> getObject(Class<?> type, Object... params) {
        try {
            return (BaseEntity<?>) UnsafeUtils.allocateInstance(type);
        } catch (InstantiationException e) {
            throw new CriticalException(e);
        }
    }
}
