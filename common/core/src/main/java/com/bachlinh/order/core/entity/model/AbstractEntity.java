package com.bachlinh.order.core.entity.model;

import com.bachlinh.order.core.entity.EntityFactory;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;

/**
 * Abstract entity, super class of all entity object available in system.
 * This entity use for auditing the entity before saving, updating to database.
 *
 * @author Hoang Minh Quan.
 */
@Setter
@Getter
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

    /**
     * Clone the object, because super interface {@link BaseEntity} extends {@link Cloneable} and constructor of all entities
     * is not public, this method must be call when create to another object. Internal use only in {@link EntityFactory}.
     *
     * @return A cloning of this object.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
}
