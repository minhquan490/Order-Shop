package com.bachlinh.order.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Transient;
import org.springframework.lang.NonNull;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.spi.FieldUpdated;

import java.sql.Timestamp;
import java.util.Collection;

/**
 * Abstract entity, super class of all entity object available in system.
 * This entity use for auditing the entity before saving, updating to database.
 *
 * @author Hoang Minh Quan.
 */
@MappedSuperclass
public abstract non-sealed class AbstractEntity implements BaseEntity {

    @Column(name = "CREATED_BY", updatable = false)
    private String createdBy;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "CREATED_DATE", updatable = false)
    private Timestamp createdDate;

    @Column(name = "MODIFIED_DATE")
    private Timestamp modifiedDate;

    @Transient
    private Collection<FieldUpdated> updatedFields;

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
    public int compareTo(@NonNull BaseEntity that) {
        if (this.equals(that)) return 0;
        if (this.hashCode() - that.hashCode() > 0) {
            return 1;
        } else {
            return -1;
        }
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

    public Collection<FieldUpdated> getUpdatedFields() {
        return updatedFields;
    }

    @ActiveReflection
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @ActiveReflection
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @ActiveReflection
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @ActiveReflection
    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @ActiveReflection
    public void setUpdatedFields(Collection<FieldUpdated> updatedFields) {
        this.updatedFields = updatedFields;
    }
}
