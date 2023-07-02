package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Validator;

@Entity
@Table(name = "ORDER_STATUS")
@Validator(validators = "com.bachlinh.order.validator.internal.OrderStatusValidator")
@ActiveReflection
public class OrderStatus extends AbstractEntity {

    @Id
    @Column(name = "ID", nullable = false, unique = true, columnDefinition = "int")
    private Integer id;

    @Column(name = "STATUS", nullable = false, length = 30)
    private String status;

    @OneToOne(optional = false, mappedBy = "orderStatus", fetch = FetchType.LAZY)
    private Order order;

    @ActiveReflection
    OrderStatus() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of OrderStatus must be int");
        }
        this.id = (Integer) id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderStatus that)) return false;
        return Objects.equal(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public Integer getId() {
        return this.id;
    }

    public String getStatus() {
        return this.status;
    }
    
    public Order getOrder() {
        return this.order;
    }

    @ActiveReflection
    public void setStatus(String status) {
        this.status = status;
    }

    @ActiveReflection
    public void setOrder(Order order) {
        this.order = order;
    }
}