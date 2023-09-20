package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.Formula;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.entity.formula.ProductEnableFormula;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
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
@Formula(processors = ProductEnableFormula.class)
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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Product other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (!Objects.equals(this$name, other$name)) return false;
        final Object this$price = this.getPrice();
        final Object other$price = other.getPrice();
        if (!Objects.equals(this$price, other$price)) return false;
        final Object this$size = this.getSize();
        final Object other$size = other.getSize();
        if (!Objects.equals(this$size, other$size)) return false;
        final Object this$color = this.getColor();
        final Object other$color = other.getColor();
        if (!Objects.equals(this$color, other$color)) return false;
        final Object this$taobaoUrl = this.getTaobaoUrl();
        final Object other$taobaoUrl = other.getTaobaoUrl();
        if (!Objects.equals(this$taobaoUrl, other$taobaoUrl)) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (!Objects.equals(this$description, other$description))
            return false;
        final Object this$orderPoint = this.getOrderPoint();
        final Object other$orderPoint = other.getOrderPoint();
        if (!Objects.equals(this$orderPoint, other$orderPoint))
            return false;
        final Object this$enabled = this.getEnabled();
        final Object other$enabled = other.getEnabled();
        return Objects.equals(this$enabled, other$enabled);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Product;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $price = this.getPrice();
        result = result * PRIME + ($price == null ? 43 : $price.hashCode());
        final Object $size = this.getSize();
        result = result * PRIME + ($size == null ? 43 : $size.hashCode());
        final Object $color = this.getColor();
        result = result * PRIME + ($color == null ? 43 : $color.hashCode());
        final Object $taobaoUrl = this.getTaobaoUrl();
        result = result * PRIME + ($taobaoUrl == null ? 43 : $taobaoUrl.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $orderPoint = this.getOrderPoint();
        result = result * PRIME + ($orderPoint == null ? 43 : $orderPoint.hashCode());
        final Object $enabled = this.getEnabled();
        result = result * PRIME + ($enabled == null ? 43 : $enabled.hashCode());
        return result;
    }
}