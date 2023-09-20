package com.bachlinh.order.web.common.entity;

import com.bachlinh.order.annotation.ResultMapper;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityMapper;
import com.bachlinh.order.entity.EntityMapperFactory;
import com.bachlinh.order.entity.mapper.AbstractEntityMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.utils.UnsafeUtils;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultEntityMapperFactory implements EntityMapperFactory {

    private final Map<Class<?>, EntityMapper<?>> sharedMappersHolder = new HashMap<>();
    private final EntityFactory entityFactory;

    public DefaultEntityMapperFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        configSharedMappers();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity<?>> EntityMapper<T> createMapper(Class<T> entityType) {
        return (EntityMapper<T>) sharedMappersHolder.compute(entityType, (aClass, entityMapper) -> {
            if (entityMapper == null) {
                throw new CriticalException(String.format("No mapper available for entity [%s]", entityType.getName()));
            }
            return entityMapper;
        });
    }

    private void configSharedMappers() {
        ApplicationScanner scanner = new ApplicationScanner();
        Collection<Class<?>> mapperClasses = scanner.findComponents()
                .stream()
                .filter(EntityMapper.class::isAssignableFrom)
                .filter(entityMapperClass -> entityMapperClass.isAnnotationPresent(ResultMapper.class))
                .toList();
        for (var mapperClass : mapperClasses) {
            Class<?> entityType = entityType(mapperClass);
            AbstractEntityMapper<?> abstractEntityMapper;
            try {
                abstractEntityMapper = (AbstractEntityMapper<?>) UnsafeUtils.allocateInstance(mapperClass);
            } catch (InstantiationException e) {
                throw new CriticalException(e);
            }
            abstractEntityMapper.setEntityFactory(entityFactory);
            abstractEntityMapper.setEntityMapperFactory(this);
            sharedMappersHolder.put(entityType, abstractEntityMapper);
        }
    }

    private Class<?> entityType(Class<?> mapperClass) {
        return ((Class<?>) ((ParameterizedType) mapperClass.getGenericSuperclass()).getActualTypeArguments()[0]);
    }
}
