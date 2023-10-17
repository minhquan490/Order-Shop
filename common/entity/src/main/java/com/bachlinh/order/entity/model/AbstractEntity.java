package com.bachlinh.order.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Transient;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.FieldUpdated;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;

/**
 * Abstract entity, super class of all entity object available in system.
 * This entity use for auditing the entity before saving, updating to database.
 *
 * @author Hoang Minh Quan.
 */
@MappedSuperclass
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
    private transient Boolean isNew;

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
    public void setCreatedBy(String createdBy) {
        if (this.createdBy != null && !Objects.equals(this.createdBy, createdBy)) {
            trackUpdatedField("CREATED_BY", this.createdBy, createdBy);
        }
        this.createdBy = createdBy;
    }

    @ActiveReflection
    public void setModifiedBy(String modifiedBy) {
        if (this.modifiedBy != null && !Objects.equals(this.modifiedBy, modifiedBy)) {
            trackUpdatedField("MODIFIED_BY", this.modifiedBy, modifiedBy);
        }
        this.modifiedBy = modifiedBy;
    }

    @ActiveReflection
    public void setCreatedDate(Timestamp createdDate) {
        if (this.createdDate != null && !Objects.equals(this.createdDate, createdDate)) {
            trackUpdatedField("CREATED_DATE", this.createdDate, createdDate);
        }
        this.createdDate = createdDate;
    }

    @ActiveReflection
    public void setModifiedDate(Timestamp modifiedDate) {
        if (this.modifiedDate != null && !Objects.equals(this.modifiedDate, modifiedDate)) {
            trackUpdatedField("MODIFIED_DATE", this.modifiedDate, modifiedDate);
        }
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
    public void setNew(Boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public boolean isNew() {
        return getIsNew() == null ? getId() == null : getIsNew();
    }

    protected void trackUpdatedField(String fieldName, Object oldValue, Object newValue) {
        FieldUpdated fieldUpdated = new FieldUpdated(fieldName, oldValue, () -> newValue);
        setUpdatedField(fieldUpdated);
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public Timestamp getCreatedDate() {
        return this.createdDate;
    }

    public Timestamp getModifiedDate() {
        return this.modifiedDate;
    }

    public List<FieldUpdated> getUpdatedFields() {
        return this.updatedFields;
    }

    @Transient
    public Boolean getIsNew() {
        return this.isNew;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity<?> that = (AbstractEntity<?>) o;
        return com.google.common.base.Objects.equal(getCreatedBy(), that.getCreatedBy()) && com.google.common.base.Objects.equal(getModifiedBy(), that.getModifiedBy()) && com.google.common.base.Objects.equal(getCreatedDate(), that.getCreatedDate()) && com.google.common.base.Objects.equal(getModifiedDate(), that.getModifiedDate());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(getCreatedBy(), getModifiedBy(), getCreatedDate(), getModifiedDate());
    }
}
