package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityMapper;
import com.bachlinh.order.entity.EntityMapperHolder;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.model.BaseEntity;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
class NativeQueryResultProcessing implements ResultListProcessing<BaseEntity<?>, Tuple> {
    private final EntityFactory entityFactory;
    private final Class<? extends BaseEntity<?>> domainClass;

    @Override
    @SuppressWarnings("unchecked")
    public List<BaseEntity<?>> process(Supplier<Collection<Tuple>> source) {
        Collection<Tuple> tuples = source.get();
        List<BaseEntity<?>> results = new LinkedList<>();
        EntityContext entityContext = entityFactory.getEntityContext(domainClass);
        EntityMapper<BaseEntity<?>> mapper = (EntityMapper<BaseEntity<?>>) ((EntityMapperHolder) entityContext).getMapper(domainClass);
        for (var tuple : tuples) {
            var entity = mapper.map(tuple);
            results.add(entity);
        }
        var entity = entityFactory.getEntity(domainClass);
        entity.setNew(false);
        return new LinkedList<>(entity.reduce(results));
    }
}
