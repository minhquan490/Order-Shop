package com.bachlinh.order.web.common.entity;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityMapperFactory;
import com.bachlinh.order.entity.mapper.AbstractEntityMapper;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.utils.UnsafeUtils;

class EntityMapperInitializer implements Initializer<AbstractEntityMapper<?>> {
    @Override
    public AbstractEntityMapper<?> getObject(Class<?> type, Object... params) {
        AbstractEntityMapper<?> abstractEntityMapper;
        try {
            abstractEntityMapper = (AbstractEntityMapper<?>) UnsafeUtils.allocateInstance(type);
        } catch (InstantiationException e) {
            throw new CriticalException(e);
        }
        abstractEntityMapper.setEntityFactory((EntityFactory) params[0]);
        abstractEntityMapper.setEntityMapperFactory((EntityMapperFactory) params[1]);
        return abstractEntityMapper;
    }
}
