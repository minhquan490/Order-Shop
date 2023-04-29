package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name = "ORDER_HISTORY")
@ActiveReflection
public class OrderHistory extends AbstractEntity {

    @Id
    @Column(name = "ID", updatable = false, columnDefinition = "int")
    private Integer id;

    @Column(name = "ORDER_TIME")
    private Timestamp orderTime;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @MapsId
    private Order order;

    @ActiveReflection
    OrderHistory() {
    }

    @ActiveReflection
    @Override
    public void setId(Object id) {
        if (id instanceof Integer) {
            this.id = (int) id;
            return;
        }
        throw new PersistenceException("Can not set id for order history, supported only integer");
    }

    @ActiveReflection
    public Integer getId() {
        return this.id;
    }

    @ActiveReflection
    public Timestamp getOrderTime() {
        return this.orderTime;
    }

    @ActiveReflection
    public Order getOrder() {
        return this.order;
    }

    @ActiveReflection
    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    @ActiveReflection
    public void setOrder(Order order) {
        this.order = order;
    }
}