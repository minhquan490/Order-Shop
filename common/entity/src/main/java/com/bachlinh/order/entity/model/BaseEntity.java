package com.bachlinh.order.entity.model;

import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;

public sealed interface BaseEntity<T> extends Serializable, Comparable<BaseEntity<?>>, Persistable<T>, Cloneable permits AbstractEntity {
    T getId();

    void setId(Object id);

    void setNew(Boolean isNew);

    Object getCreatedBy();

    Object getModifiedBy();

    Timestamp getCreatedDate();

    Timestamp getModifiedDate();

    <U extends BaseEntity<T>> Collection<U> reduce(Collection<BaseEntity<?>> entities);
}
