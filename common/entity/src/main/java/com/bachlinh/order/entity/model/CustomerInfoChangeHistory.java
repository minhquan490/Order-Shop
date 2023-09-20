package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "CUSTOMER_INFORMATION_CHANGE_HISTORY", indexes = @Index(name = "idx_customer_history_id", columnList = "CUSTOMER_ID"))
@ActiveReflection
@Label("CIH-")
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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof CustomerInfoChangeHistory other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$oldValue = this.getOldValue();
        final Object other$oldValue = other.getOldValue();
        if (!Objects.equals(this$oldValue, other$oldValue)) return false;
        final Object this$fieldName = this.getFieldName();
        final Object other$fieldName = other.getFieldName();
        if (!Objects.equals(this$fieldName, other$fieldName)) return false;
        final Object this$timeUpdate = this.getTimeUpdate();
        final Object other$timeUpdate = other.getTimeUpdate();
        return Objects.equals(this$timeUpdate, other$timeUpdate);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CustomerInfoChangeHistory;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $oldValue = this.getOldValue();
        result = result * PRIME + ($oldValue == null ? 43 : $oldValue.hashCode());
        final Object $fieldName = this.getFieldName();
        result = result * PRIME + ($fieldName == null ? 43 : $fieldName.hashCode());
        final Object $timeUpdate = this.getTimeUpdate();
        result = result * PRIME + ($timeUpdate == null ? 43 : $timeUpdate.hashCode());
        return result;
    }
}
