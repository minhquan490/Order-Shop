package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableFullTextSearch;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.core.annotation.FullTextField;
import com.bachlinh.order.core.annotation.Label;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;
import com.bachlinh.order.repository.formula.ProductEnableFormula;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
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
@EnableFullTextSearch
@ActiveReflection
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class, ProductEnableFormula.class})
public class Product extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "NAME", unique = true, nullable = false, columnDefinition = "nvarchar(100)")
    @FullTextField
    @ActiveReflection
    private String name;

    @Column(name = "PRICE", nullable = false)
    private Integer price;

    @Column(name = "SIZE", length = 3, nullable = false)
    @FullTextField
    @ActiveReflection
    private String size;

    @Column(name = "COLOR", columnDefinition = "nvarchar(30)", nullable = false)
    @FullTextField
    @ActiveReflection
    private String color;

    @Column(name = "TAO_BAO_URL")
    private String taobaoUrl;

    @Column(name = "DESCRIPTION", columnDefinition = "nvarchar(400)")
    private String description;

    @Column(name = "ORDER_POINT")
    private Integer orderPoint;

    @Column(name = "ENABLED", columnDefinition = "bit", nullable = false)
    private Boolean enabled;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product", orphanRemoval = true)
    private Collection<ProductMedia> medias = new HashSet<>();

    @ManyToMany(mappedBy = "products")
    private Collection<Category> categories = new HashSet<>();

    @ManyToMany(mappedBy = "products")
    private Collection<Cart> carts = new HashSet<>();

    @ActiveReflection
    protected Product() {
    }

    public boolean isEnabled() {
        return getEnabled();
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
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        } else {
            Deque<Product> results = new LinkedList<>();
            Product first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (Product) entity;
                } else {
                    Product casted = (Product) entity;
                    if (casted.getCategories().isEmpty() && casted.getMedias().isEmpty() && casted.getCarts().isEmpty()) {
                        results.add(casted);
                    } else {
                        first.getCategories().addAll(casted.getCategories());
                        first.getMedias().addAll(casted.getMedias());
                        first.getCarts().addAll(casted.getCarts());
                    }
                }
            }
            results.addFirst(first);
            return (Collection<U>) results;
        }
    }

    @ActiveReflection
    public void setName(String name) {
        if (this.name != null && !this.name.equals(name)) {
            trackUpdatedField("NAME", this.name, name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setPrice(Integer price) {
        if (this.price != null && !this.price.equals(price)) {
            trackUpdatedField("PRICE", this.price, price);
        }
        this.price = price;
    }

    @ActiveReflection
    public void setSize(String size) {
        if (this.size != null && !this.size.equals(size)) {
            trackUpdatedField("SIZE", this.size, size);
        }
        this.size = size;
    }

    @ActiveReflection
    public void setColor(String color) {
        if (this.color != null && !this.color.equals(color)) {
            trackUpdatedField("COLOR", this.color, color);
        }
        this.color = color;
    }

    @ActiveReflection
    public void setTaobaoUrl(String taobaoUrl) {
        if (this.taobaoUrl != null && !this.taobaoUrl.equals(taobaoUrl)) {
            trackUpdatedField("TAO_BAO_URL", this.taobaoUrl, taobaoUrl);
        }
        this.taobaoUrl = taobaoUrl;
    }

    @ActiveReflection
    public void setDescription(String description) {
        if (this.description != null && !this.description.equals(description)) {
            trackUpdatedField("DESCRIPTION", this.description, description);
        }
        this.description = description;
    }

    @ActiveReflection
    public void setOrderPoint(Integer orderPoint) {
        if (this.orderPoint != null && !this.orderPoint.equals(orderPoint)) {
            trackUpdatedField("ORDER_POINT", this.orderPoint, orderPoint);
        }
        this.orderPoint = orderPoint;
    }

    @ActiveReflection
    public void setEnabled(Boolean enabled) {
        if (this.enabled != null && this.enabled.equals(enabled)) {
            trackUpdatedField("ENABLED", this.enabled, enabled);
        }
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

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Integer getPrice() {
        return this.price;
    }

    public String getSize() {
        return this.size;
    }

    public String getColor() {
        return this.color;
    }

    public String getTaobaoUrl() {
        return this.taobaoUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getOrderPoint() {
        return this.orderPoint;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public Collection<ProductMedia> getMedias() {
        return this.medias;
    }

    public Collection<Category> getCategories() {
        return this.categories;
    }

    public Collection<Cart> getCarts() {
        return this.carts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        if (!super.equals(o)) return false;
        return Objects.equal(getId(), product.getId()) && Objects.equal(getName(), product.getName()) && Objects.equal(getPrice(), product.getPrice()) && Objects.equal(getSize(), product.getSize()) && Objects.equal(getColor(), product.getColor()) && Objects.equal(getTaobaoUrl(), product.getTaobaoUrl()) && Objects.equal(getDescription(), product.getDescription()) && Objects.equal(getOrderPoint(), product.getOrderPoint()) && Objects.equal(isEnabled(), product.isEnabled()) && Objects.equal(getMedias(), product.getMedias()) && Objects.equal(getCategories(), product.getCategories()) && Objects.equal(getCarts(), product.getCarts());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getId(), getName(), getPrice(), getSize(), getColor(), getTaobaoUrl(), getDescription(), getOrderPoint(), isEnabled(), getMedias(), getCategories(), getCarts());
    }
}