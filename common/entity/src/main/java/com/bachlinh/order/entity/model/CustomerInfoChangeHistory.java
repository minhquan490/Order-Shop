package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;

@Entity
@Table(name = "CUSTOMER_INFORMATION_CHANGE_HISTORY", indexes = @Index(name = "idx_customer_history_id", columnList = "CUSTOMER_ID"))
@Getter
@ActiveReflection
@Label("CIH-")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false)
    private Customer customer;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of CustomerInfoChange must be string");
    }

    @ActiveReflection
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    @ActiveReflection
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @ActiveReflection
    public void setTimeUpdate(Timestamp timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
