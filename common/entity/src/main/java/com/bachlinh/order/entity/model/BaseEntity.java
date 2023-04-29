package com.bachlinh.order.entity.model;

import java.io.Serializable;
import java.sql.Timestamp;

public sealed interface BaseEntity extends Serializable, Comparable<BaseEntity>, Cloneable permits AbstractEntity {
    Object getId();

    void setId(Object id);

    Object getCreatedBy();

    Object getModifiedBy();

    Timestamp getCreatedDate();

    Timestamp getModifiedDate();
}
