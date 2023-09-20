package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityMapper;
import com.bachlinh.order.entity.EntityMapperFactory;
import com.bachlinh.order.entity.model.BaseEntity;
import jakarta.persistence.Tuple;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public abstract class AbstractEntityMapper<T extends BaseEntity<?>> implements EntityMapper<T> {

    private EntityFactory entityFactory;

    private EntityMapperFactory entityMapperFactory;

    @Override
    public final T map(Tuple resultSet) {
        Queue<MappingObject> parsed = parseTuple(resultSet);
        return map(parsed);
    }

    @Override
    public final T map(Queue<MappingObject> resultSet) {
        EntityWrapper wrapper = internalMapping(resultSet);
        T result = wrapper.getEntity();
        if (!wrapper.isTouched()) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public final boolean canMap(Collection<MappingObject> testTarget) {
        String tableName = getTableName();
        Predicate<MappingObject> columnNameMatcher = mappingObject -> {
            String columnName = mappingObject.columnName();
            return columnName.split("\\.")[0].equals(tableName);
        };
        return testTarget.stream().anyMatch(columnNameMatcher);
    }

    protected abstract String getTableName();

    protected abstract EntityWrapper internalMapping(Queue<MappingObject> resultSet);

    protected abstract void setData(T target, MappingObject mappingObject, AtomicBoolean wrappedTouched);

    private Queue<MappingObject> parseTuple(Tuple target) {
        Queue<MappingObject> mappingObjectQueue = new LinkedList<>();
        for (var ele : target.getElements()) {
            var mappedObject = new MappingObject(ele.getAlias(), target.get(ele.getAlias(), ele.getJavaType()));
            mappingObjectQueue.add(mappedObject);
        }
        return mappingObjectQueue;
    }

    protected EntityFactory getEntityFactory() {
        return this.entityFactory;
    }

    protected EntityMapperFactory getEntityMapperFactory() {
        return this.entityMapperFactory;
    }

    public void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    public void setEntityMapperFactory(EntityMapperFactory entityMapperFactory) {
        this.entityMapperFactory = entityMapperFactory;
    }

    protected class EntityWrapper {
        private final T entity;

        private boolean isTouched;

        EntityWrapper(T entity) {
            this.entity = entity;
        }

        public T getEntity() {
            return this.entity;
        }

        public boolean isTouched() {
            return this.isTouched;
        }

        public void setTouched(boolean isTouched) {
            this.isTouched = isTouched;
        }
    }
}
