package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;

@Entity
@Table(name = "CART", indexes = @Index(name = "idx_cart_customer", columnList = "CUSTOMER_ID"))
@Label("CRT-")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@Getter
public class Cart extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, columnDefinition = "varchar(32)")
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", unique = true, nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "cart", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Collection<CartDetail> cartDetails = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "PRODUCT_CART",
            joinColumns = @JoinColumn(name = "CART_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID"),
            indexes = @Index(name = "idx_product_cart", columnList = "CART_ID")
    )
    private Collection<Product> products = new HashSet<>();

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Id of cart must be string");
        }
        this.id = (String) id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        } else {
            Deque<Cart> results = new LinkedList<>();
            Cart first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (Cart) entity;
                } else {
                    Cart casted = (Cart) entity;
                    if (casted.getProducts().isEmpty() && casted.getCartDetails().isEmpty()) {
                        results.add(casted);
                    } else {
                        first.getCartDetails().addAll(casted.getCartDetails());
                        first.getProducts().addAll(casted.getProducts());
                    }
                }
            }
            results.addFirst(first);
            return (Collection<U>) results;
        }
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        if (this.customer != null && !this.customer.getId().equals(Objects.requireNonNull(customer).getId())) {
            trackUpdatedField("CUSTOMER_ID", this.customer.getId(), customer.getId());
        }
        this.customer = customer;
    }

    @ActiveReflection
    public void setCartDetails(@NonNull Collection<CartDetail> cartDetails) {
        this.cartDetails = cartDetails;
    }

    @ActiveReflection
    public void setProducts(@NonNull Collection<Product> products) {
        this.products = products;
    }
}
