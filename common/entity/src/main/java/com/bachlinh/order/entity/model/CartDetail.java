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

@Entity
@Table(name = "CART_DETAIL", indexes = @Index(name = "idx_cart_cart-detail", columnList = "CART_ID"))
@ActiveReflection
public class CartDetail extends AbstractEntity {

    @Id
    @Column(name = "ID", columnDefinition = "int", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "AMOUNT", columnDefinition = "int")
    private Integer amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID", nullable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "CART_ID", nullable = false, updatable = false)
    private Cart cart;

    @Override
    public Object getId() {
        return id;
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of cart must be int");
    }

    public Integer getAmount() {
        return amount;
    }

    @ActiveReflection
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Product getProduct() {
        return product;
    }

    @ActiveReflection
    public void setProduct(Product product) {
        this.product = product;
    }

    public Cart getCart() {
        return cart;
    }

    @ActiveReflection
    public void setCart(Cart cart) {
        this.cart = cart;
    }
}
