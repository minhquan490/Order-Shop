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
@Table(name = "PRODUCT_MEDIA", indexes = @Index(name = "idx_product_media_product", columnList = "PRODUCT_ID"))
@Validator(validators = "com.bachlinh.order.core.entity.validator.internal.ProductPictureValidator")
@ActiveReflection
public class ProductMedia extends AbstractEntity {

    @Id
    @Column(name = "ID", nullable = false, unique = true, columnDefinition = "int")
    private Integer id;

    @Column(name = "URL", nullable = false, length = 100)
    private String url;

    @Column(name = "CONTENT_TYPE", nullable = false, length = 100)
    private String contentType;

    @Column(name = "CONTENT_LENGTH", nullable = false, length = 100)
    private long contentLength;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @ActiveReflection
    ProductMedia() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductMedia that)) return false;
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
    public String getUrl() {
        return this.url;
    }

    @ActiveReflection
    public String getContentType() {
        return this.contentType;
    }

    @ActiveReflection
    public long getContentLength() {
        return this.contentLength;
    }

    @ActiveReflection
    public Product getProduct() {
        return this.product;
    }

    @ActiveReflection
    public void setUrl(String url) {
        this.url = url;
    }

    @ActiveReflection
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @ActiveReflection
    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    @ActiveReflection
    public void setProduct(Product product) {
        this.product = product;
    }
}