package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.annotation.Validator;
import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Label("CMA-")
@Table(name = "CUSTOMER_MEDIA")
@Entity
@ActiveReflection
@Validator(validators = "com.bachlinh.order.validate.validator.internal.CustomerMediaValidator")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
public class CustomerMedia extends AbstractEntity {

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
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @ActiveReflection
    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    @ActiveReflection
    public void setUrl(String url) {
        this.url = url;
    }

    @ActiveReflection
    public void setContentType(String contentType) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerMedia that)) return false;
        return getContentLength() == that.getContentLength() && Objects.equal(getId(), that.getId()) && Objects.equal(getUrl(), that.getUrl()) && Objects.equal(getContentType(), that.getContentType()) && Objects.equal(getCustomer(), that.getCustomer());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getUrl(), getContentType(), getContentLength(), getCustomer());
    }
}
