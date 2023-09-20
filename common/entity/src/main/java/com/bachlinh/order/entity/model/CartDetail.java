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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "CART_DETAIL", indexes = @Index(name = "idx_cart_cartDetail", columnList = "CART_ID"))
@ActiveReflection
public class CartDetail extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", columnDefinition = "int", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "AMOUNT", columnDefinition = "int", nullable = false)
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "CART_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Cart cart;

    @ActiveReflection
    protected CartDetail() {
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

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setAmount(Integer amount) {
        if (this.amount != null && !this.amount.equals(amount)) {
            trackUpdatedField("AMOUNT", this.amount, amount);
        }
        this.amount = amount;
    }

    @ActiveReflection
    public void setProduct(Product product) {
        if (this.product != null && !Objects.requireNonNull(this.product.getId()).equals(Objects.requireNonNull(product).getId())) {
            trackUpdatedField("PRODUCT_ID", this.product.getId(), product.getId());
        }
        this.product = product;
    }

    @ActiveReflection
    public void setCart(Cart cart) {
        if (this.cart != null && !Objects.requireNonNull(this.cart.getId()).equals(Objects.requireNonNull(cart).getId())) {
            trackUpdatedField("CART_ID", this.cart.getId(), cart.getId());
        }
        this.cart = cart;
    }

    public Integer getId() {
        return this.id;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public Product getProduct() {
        return this.product;
    }

    public Cart getCart() {
        return this.cart;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof CartDetail other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$amount = this.getAmount();
        final Object other$amount = other.getAmount();
        return Objects.equals(this$amount, other$amount);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CartDetail;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $amount = this.getAmount();
        result = result * PRIME + ($amount == null ? 43 : $amount.hashCode());
        return result;
    }
}
