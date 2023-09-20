package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import java.util.Collection;
import java.util.Objects;

@Label("CMA-")
@Table(name = "CUSTOMER_MEDIA")
@Entity
@ActiveReflection
public class CustomerMedia extends AbstractEntity<String> {

    @Id
    @Column(name = "ID")
    String id;

    @Column(name = "URL", nullable = false, length = 100)
    private String url;

    @Column(name = "CONTENT_TYPE", nullable = false, length = 100)
    private String contentType;

    @Column(name = "CONTENT_LENGTH", nullable = false)
    private Long contentLength;

    @OneToOne(optional = false, mappedBy = "customerMedia", fetch = FetchType.LAZY)
    private Customer customer;

    @ActiveReflection
    protected CustomerMedia() {
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @ActiveReflection
    public void setContentLength(long contentLength) {
        if (this.contentLength != null && !this.contentLength.equals(contentLength)) {
            trackUpdatedField("CONTENT_LENGTH", this.contentLength, contentLength);
        }
        this.contentLength = contentLength;
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

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of CustomerMedia must be string");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    public String getId() {
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

    public Customer getCustomer() {
        return this.customer;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof CustomerMedia other)) return false;
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
        return other instanceof CustomerMedia;
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
