package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.core.annotation.Label;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;

import java.util.Collection;

@Label("CMA-")
@Table(name = "CUSTOMER_MEDIA")
@Entity
@ActiveReflection
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerMedia that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getUrl(), that.getUrl()) && Objects.equal(getContentType(), that.getContentType()) && Objects.equal(getContentLength(), that.getContentLength()) && Objects.equal(getCustomer(), that.getCustomer());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getId(), getUrl(), getContentType(), getContentLength(), getCustomer());
    }
}
