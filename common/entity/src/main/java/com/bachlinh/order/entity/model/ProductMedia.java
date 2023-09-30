package com.bachlinh.order.entity.model;

import com.bachlinh.order.core.annotation.ActiveReflection;
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
@Table(name = "PRODUCT_MEDIA", indexes = @Index(name = "idx_product_media_product", columnList = "PRODUCT_ID"))
@ActiveReflection
public class ProductMedia extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", nullable = false, unique = true, columnDefinition = "int")
    private Integer id;

    @Column(name = "URL", nullable = false, length = 100)
    private String url;

    @Column(name = "CONTENT_TYPE", nullable = false, length = 100)
    private String contentType;

    @Column(name = "CONTENT_LENGTH", nullable = false)
    private Long contentLength;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    @Fetch(FetchMode.JOIN)
    private Product product;

    @ActiveReflection
    protected ProductMedia() {
    }

    @ActiveReflection
    @Override
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of product picture must be int");
        }
        this.id = (Integer) id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setUrl(String url) {
        if (this.url != null && !this.url.equals(url)) {
            trackUpdatedField("URL", this.url, url);
        }
        this.url = url;
    }

    @ActiveReflection
    public void setContentType(String contentType) {
        if (this.contentType != null && !this.contentType.equals(contentType)) {
            trackUpdatedField("CONTENT_TYPE", this.contentType, contentType);
        }
        this.contentType = contentType;
    }

    @ActiveReflection
    public void setContentLength(Long contentLength) {
        if (this.contentLength != null && !this.contentLength.equals(contentLength)) {
            trackUpdatedField("CONTENT_LENGTH", this.contentLength, contentLength);
        }
        this.contentLength = contentLength;
    }

    @ActiveReflection
    public void setProduct(Product product) {
        if (this.product != null && !Objects.requireNonNull(this.product.getId()).equals(product.getId())) {
            trackUpdatedField("PRODUCT_ID", this.product.getId(), product.getId());
        }
        this.product = product;
    }

    public Integer getId() {
        return this.id;
    }

    public String getUrl() {
        return this.url;
    }

    public String getContentType() {
        return this.contentType;
    }

    public Long getContentLength() {
        return this.contentLength;
    }

    public Product getProduct() {
        return this.product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductMedia that)) return false;
        if (!super.equals(o)) return false;
        return com.google.common.base.Objects.equal(getId(), that.getId()) && com.google.common.base.Objects.equal(getUrl(), that.getUrl()) && com.google.common.base.Objects.equal(getContentType(), that.getContentType()) && com.google.common.base.Objects.equal(getContentLength(), that.getContentLength()) && com.google.common.base.Objects.equal(getProduct(), that.getProduct());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(super.hashCode(), getId(), getUrl(), getContentType(), getContentLength(), getProduct());
    }
}