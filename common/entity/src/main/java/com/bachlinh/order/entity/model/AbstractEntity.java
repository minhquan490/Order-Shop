package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Formula;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityMapper;
import com.bachlinh.order.entity.context.FieldUpdated;
import com.bachlinh.order.entity.formula.internal.CommonFieldSelectFormula;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Transient;
import jakarta.persistence.Tuple;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Abstract entity, super class of all entity object available in system.
 * This entity use for auditing the entity before saving, updating to database.
 *
 * @author Hoang Minh Quan.
 */
@MappedSuperclass
@Getter
@EqualsAndHashCode
@Formula(processor = CommonFieldSelectFormula.class)
public abstract non-sealed class AbstractEntity<T> implements BaseEntity<T> {

    @Column(name = "CREATED_BY", updatable = false)
    private String createdBy;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "CREATED_DATE", updatable = false)
    private Timestamp createdDate;

    @Column(name = "MODIFIED_DATE")
    private Timestamp modifiedDate;

    @Transient
    private transient List<FieldUpdated> updatedFields;

    @Transient
    private transient boolean isNew = false;

    /**
     * Clone the object, because super interface {@link BaseEntity} extends {@link Cloneable} and constructor of all entities
     * is not public, this method must be call when create to another object. Internal use only in {@link EntityFactory}.
     *
     * @return A cloning of this object.
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new PersistenceException(String.format("Can not clone entity [%s]", getClass().getName()));
        }
    }

    @Override
    public int compareTo(@NonNull BaseEntity<?> that) {
        if (this.equals(that)) return 0;
        if (this.hashCode() - that.hashCode() > 0) {
            return 1;
        } else {
            return -1;
        }
    }

    @ActiveReflection
    public void setCreatedBy(@NonNull String createdBy) {
        this.createdBy = createdBy;
    }

    @ActiveReflection
    public void setModifiedBy(@NonNull String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @ActiveReflection
    public void setCreatedDate(@NonNull Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @ActiveReflection
    public void setModifiedDate(@NonNull Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setUpdatedFields(List<FieldUpdated> updatedFields) {
        this.updatedFields = updatedFields;
    }

    public void setUpdatedField(FieldUpdated updatedField) {
        if (this.updatedFields == null) {
            this.updatedFields = new LinkedList<>();
        }
        this.updatedFields.add(updatedField);
    }

    @Override
    @ActiveReflection
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    protected void trackUpdatedField(String fieldName, Object oldValue, Object newValue) {
        FieldUpdated fieldUpdated = new FieldUpdated(fieldName, oldValue, () -> newValue);
        setUpdatedField(fieldUpdated);
    }

    protected Queue<EntityMapper.MappingObject> parseTuple(Tuple target) {
        Queue<EntityMapper.MappingObject> mappingObjectQueue = new LinkedList<>();
        for (var ele : target.getElements()) {
            var mappedObject = new EntityMapper.MappingObject(ele.getAlias(), target.get(ele.getAlias(), ele.getJavaType()));
            mappingObjectQueue.add(mappedObject);
        }
        return mappingObjectQueue;
    }
}
