package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Validator;
import com.google.common.base.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDER_DETAIL", indexes = @Index(name = "idx_order", columnList = "ORDER_ID"))
@Validator(validators = "com.bachlinh.order.validator.internal.OrderDetailValidator")
@ActiveReflection
public class OrderDetail extends AbstractEntity {

    @Id
    @Column(name = "ID", updatable = false, unique = true, columnDefinition = "int")
    private Integer id;

    @Column(name = "AMOUNT", nullable = false)
    private Integer amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID", nullable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private Order order;

    @ActiveReflection
    OrderDetail() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of OrderDetail must be int");
        }
        this.id = (Integer) id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderDetail that)) return false;
        return Objects.equal(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @ActiveReflection
    public Integer getId() {
        return this.id;
    }

    @ActiveReflection
    public int getAmount() {
        return this.amount;
    }

    @ActiveReflection
    public Product getProduct() {
        return this.product;
    }

    @ActiveReflection
    public Order getOrder() {
        return this.order;
    }

    @ActiveReflection
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @ActiveReflection
    public void setProduct(Product product) {
        this.product = product;
    }

    @ActiveReflection
    public void setOrder(Order order) {
        this.order = order;
    }
}
