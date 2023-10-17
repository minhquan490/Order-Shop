package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.BaseEntity;
import jakarta.persistence.Tuple;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public interface ResultListProcessing<T, U> {
    List<T> process(Supplier<Collection<U>> source);

    static ResultListProcessing<BaseEntity<?>, Tuple> nativeProcessing(EntityFactory entityFactory, Class<? extends BaseEntity<?>> domainClass) {
        return new NativeQueryResultProcessing(entityFactory, domainClass);
    }
}
