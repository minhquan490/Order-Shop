package com.bachlinh.order.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;

import java.sql.Timestamp;

@Entity
@Table(name = "CUSTOMER_INFORMATION_CHANGE_HISTORY")
@Getter
@ActiveReflection
@Label("CIH-")
@NoArgsConstructor(access = AccessLevel.NONE, onConstructor_ = @ActiveReflection)
public class CustomerInfoChangeHistory extends AbstractEntity {

    @Id
    @Column(name = "ID", updatable = false, unique = true, nullable = false)
    private String id;

    @Column(name = "OLD_VALUE", updatable = false, nullable = false)
    @Setter(onMethod_ = @ActiveReflection)
    private String oldValue;

    @Column(name = "FIELD_NAME", updatable = false, nullable = false)
    @Setter(onMethod_ = @ActiveReflection)
    private String fieldName;

    @Column(name = "TIME_UPDATE", updatable = false, nullable = false)
    @Setter(onMethod_ = @ActiveReflection)
    private Timestamp timeUpdate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false)
    @Setter(onMethod_ = @ActiveReflection)
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
}
