package com.bachlinh.order.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.core.annotation.Label;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "CUSTOMER_INFORMATION_CHANGE_HISTORY", indexes = @Index(name = "idx_customer_history_id", columnList = "CUSTOMER_ID"))
@ActiveReflection
@Label("CIH-")
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
public class CustomerInfoChangeHistory extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", updatable = false, unique = true, nullable = false)
    private String id;

    @Column(name = "OLD_VALUE", updatable = false, nullable = false)
    private String oldValue;

    @Column(name = "FIELD_NAME", updatable = false, nullable = false)
    private String fieldName;

    @Column(name = "TIME_UPDATE", updatable = false, nullable = false)
    private Timestamp timeUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer customer;

    @ActiveReflection
    protected CustomerInfoChangeHistory() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of CustomerInfoChange must be string");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setOldValue(String oldValue) {
        if (this.oldValue != null && !this.oldValue.equals(oldValue)) {
            trackUpdatedField("OLD_VALUE", this.oldValue, oldValue);
        }
        this.oldValue = oldValue;
    }

    @ActiveReflection
    public void setFieldName(String fieldName) {
        if (this.fieldName != null && !this.fieldName.equals(fieldName)) {
            trackUpdatedField("FIELD_NAME", this.fieldName, fieldName);
        }
        this.fieldName = fieldName;
    }

    @ActiveReflection
    public void setTimeUpdate(Timestamp timeUpdate) {
        if (this.timeUpdate != null && !this.timeUpdate.equals(timeUpdate)) {
            trackUpdatedField("TIME_UPDATE", this.timeUpdate, timeUpdate);
        }
        this.timeUpdate = timeUpdate;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        if (this.customer != null && this.customer.getId().equals(Objects.requireNonNull(customer).getId())) {
            trackUpdatedField("CUSTOMER_ID", this.customer.getId(), customer.getId());
        }
        this.customer = customer;
    }

    public String getId() {
        return this.id;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public Timestamp getTimeUpdate() {
        return this.timeUpdate;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerInfoChangeHistory that)) return false;
        if (!super.equals(o)) return false;
        return com.google.common.base.Objects.equal(getId(), that.getId()) && com.google.common.base.Objects.equal(getOldValue(), that.getOldValue()) && com.google.common.base.Objects.equal(getFieldName(), that.getFieldName()) && com.google.common.base.Objects.equal(getTimeUpdate(), that.getTimeUpdate()) && com.google.common.base.Objects.equal(getCustomer(), that.getCustomer());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(super.hashCode(), getId(), getOldValue(), getFieldName(), getTimeUpdate(), getCustomer());
    }
}
