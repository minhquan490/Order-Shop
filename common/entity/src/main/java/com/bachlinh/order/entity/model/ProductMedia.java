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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ProductMedia other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$url = this.getUrl();
        final Object other$url = other.getUrl();
        if (!Objects.equals(this$url, other$url)) return false;
        final Object this$contentType = this.getContentType();
        final Object other$contentType = other.getContentType();
        if (!Objects.equals(this$contentType, other$contentType))
            return false;
        final Object this$contentLength = this.getContentLength();
        final Object other$contentLength = other.getContentLength();
        return Objects.equals(this$contentLength, other$contentLength);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ProductMedia;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $url = this.getUrl();
        result = result * PRIME + ($url == null ? 43 : $url.hashCode());
        final Object $contentType = this.getContentType();
        result = result * PRIME + ($contentType == null ? 43 : $contentType.hashCode());
        final Object $contentLength = this.getContentLength();
        result = result * PRIME + ($contentLength == null ? 43 : $contentLength.hashCode());
        return result;
    }
}