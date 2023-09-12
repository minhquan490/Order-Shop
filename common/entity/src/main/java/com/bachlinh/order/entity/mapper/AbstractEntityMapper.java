package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityMapper;
import com.bachlinh.order.entity.EntityMapperFactory;
import com.bachlinh.order.entity.model.BaseEntity;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

@Getter(AccessLevel.PROTECTED)
public abstract class AbstractEntityMapper<T extends BaseEntity<?>> implements EntityMapper<T> {

    @Setter
    private EntityFactory entityFactory;

    @Setter
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

    @Getter
    protected class EntityWrapper {
        private final T entity;

        @Setter
        private boolean isTouched;

        EntityWrapper(T entity) {
            this.entity = entity;
        }
    }
}
