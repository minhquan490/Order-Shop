package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.annotation.Trigger;
import com.bachlinh.order.annotation.Validator;

import java.util.Collection;
import java.util.Set;

@Entity
@Table(
        name = "PRODUCT",
        indexes = {
                @Index(name = "idx_product_name", columnList = "NAME", unique = true),
                @Index(name = "idx_product_created_date", columnList = "CREATED_DATE")
        }
)
@Label("PRD-")
@Validator(validators = "com.bachlinh.order.validator.internal.ProductValidator")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "product")
@EnableFullTextSearch
@Trigger(triggers = "com.bachlinh.order.trigger.internal.ProductIndexTrigger")
@ActiveReflection
public class Product extends AbstractEntity {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "NAME", unique = true, nullable = false, columnDefinition = "nvarchar(100)")
    @FullTextField
    private String name;

    @Column(name = "PRICE", nullable = false)
    private int price;

    @Column(name = "SIZE", length = 3, nullable = false)
    @FullTextField
    private String size;

    @Column(name = "COLOR", columnDefinition = "nvarchar(30)", nullable = false)
    @FullTextField
    private String color;

    @Column(name = "TAO_BAO_URL")
    private String taobaoUrl;

    @Column(name = "DESCRIPTION", columnDefinition = "nvarchar(400)")
    private String description;

    @Column(name = "ORDER_POINT")
    private Integer orderPoint;

    @Column(name = "ENABLED", columnDefinition = "bit", nullable = false)
    private boolean enabled = true;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product", orphanRemoval = true)
    private Collection<ProductMedia> medias;

    @ManyToMany(mappedBy = "products")
    private Collection<Category> categories;

    @ManyToMany(mappedBy = "products")
    private Collection<Cart> carts;

    @ActiveReflection
    Product() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Id of product must be string");
        }
        this.id = (String) id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equal(getId(), product.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @ActiveReflection
    public String getId() {
        return this.id;
    }

    @ActiveReflection
    public String getName() {
        return this.name;
    }

    @ActiveReflection
    public int getPrice() {
        return this.price;
    }

    @ActiveReflection
    public String getSize() {
        return this.size;
    }

    @ActiveReflection
    public String getColor() {
        return this.color;
    }

    @ActiveReflection
    public String getTaobaoUrl() {
        return this.taobaoUrl;
    }

    @ActiveReflection
    public String getDescription() {
        return this.description;
    }

    @ActiveReflection
    public Integer getOrderPoint() {
        return this.orderPoint;
    }

    @ActiveReflection
    public boolean isEnabled() {
        return this.enabled;
    }

    @ActiveReflection
    public Collection<ProductMedia> getMedias() {
        return this.medias;
    }

    @ActiveReflection
    public Collection<Category> getCategories() {
        return this.categories;
    }

    @ActiveReflection
    public Collection<Cart> getCarts() {
        return this.carts;
    }

    @ActiveReflection
    public void setName(String name) {
        this.name = name;
    }

    @ActiveReflection
    public void setPrice(int price) {
        this.price = price;
    }

    @ActiveReflection
    public void setSize(String size) {
        this.size = size;
    }

    @ActiveReflection
    public void setColor(String color) {
        this.color = color;
    }

    @ActiveReflection
    public void setTaobaoUrl(String taobaoUrl) {
        this.taobaoUrl = taobaoUrl;
    }

    @ActiveReflection
    public void setDescription(String description) {
        this.description = description;
    }

    @ActiveReflection
    public void setOrderPoint(Integer orderPoint) {
        this.orderPoint = orderPoint;
    }

    @ActiveReflection
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @ActiveReflection
    public void setMedias(Set<ProductMedia> medias) {
        this.medias = medias;
    }

    @ActiveReflection
    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @ActiveReflection
    public void setCarts(Set<Cart> carts) {
        this.carts = carts;
    }
}