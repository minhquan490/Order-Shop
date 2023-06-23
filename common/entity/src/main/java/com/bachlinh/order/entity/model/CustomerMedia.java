package com.bachlinh.order.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;

@Label("CMA-")
@Table(name = "CUSTOMER_MEDIA")
@Entity
@ActiveReflection
public class CustomerMedia extends AbstractEntity {

    @Id
    @Column(name = "ID")
    String id;

    @Column(name = "URL", nullable = false, length = 100)
    private String url;

    @Column(name = "CONTENT_TYPE", nullable = false, length = 100)
    private String contentType;

    @Column(name = "CONTENT_LENGTH", nullable = false)
    private long contentLength;

    @OneToOne(optional = false, mappedBy = "customerMedia", fetch = FetchType.LAZY)
    private Customer customer;

    @ActiveReflection
    CustomerMedia() {
    }

    @Override
    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public Customer getCustomer() {
        return customer;
    }

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
}
