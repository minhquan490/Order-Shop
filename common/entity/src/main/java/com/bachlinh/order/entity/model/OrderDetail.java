package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "ORDER_DETAIL", indexes = @Index(name = "idx_order", columnList = "ORDER_ID"))
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class OrderDetail extends AbstractEntity<Integer> {

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

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of OrderDetail must be int");
        }
        this.id = (Integer) id;
    }

    @ActiveReflection
    public void setAmount(Integer amount) {
        if (this.amount != null && !this.amount.equals(amount)) {
            trackUpdatedField("AMOUNT", this.amount.toString());
        }
        this.amount = amount;
    }

    @ActiveReflection
    public void setProduct(Product product) {
        if (this.product != null && !this.product.getId().equals(product.getId())) {
            trackUpdatedField("PRODUCT_ID", product.getId());
        }
        this.product = product;
    }

    @ActiveReflection
    public void setOrder(Order order) {
        if (this.order != null && !this.order.getId().equals(order.getId())) {
            trackUpdatedField("ORDER_ID", this.order.getId());
        }
        this.order = order;
    }
}
