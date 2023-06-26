package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import com.bachlinh.order.annotation.ActiveReflection;

import java.sql.Date;

@Entity
@Table(name = "CUSTOMER_ACCESS_HISTORY", indexes = @Index(name = "idx_access_history_customer", columnList = "CUSTOMER_ID"))
@ActiveReflection
public class CustomerAccessHistory extends AbstractEntity {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, columnDefinition = "int")
    private Integer id;

    @Column(name = "PATH_REQUEST", length = 50)
    private String pathRequest;

    // Use RequestType enum
    @Column(name = "REQUEST_TYPE", length = 15, nullable = false, updatable = false)
    private String requestType;

    @Column(name = "REQUEST_TIME", nullable = false)
    private Date requestTime;

    @Column(name = "REQUEST_CONTENT", columnDefinition = "nvarchar")
    private String requestContent;

    @Column(name = "REMOVED_TIME", nullable = false)
    private Date removeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false)
    private Customer customer;

    @ActiveReflection
    CustomerAccessHistory() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of history must be int");
        }
        this.id = (Integer) id;
    }

    @ActiveReflection
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerAccessHistory that = (CustomerAccessHistory) o;
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
    public String getPathRequest() {
        return this.pathRequest;
    }

    @ActiveReflection
    public String getRequestType() {
        return this.requestType;
    }

    @ActiveReflection
    public Date getRequestTime() {
        return this.requestTime;
    }

    @ActiveReflection
    public String getRequestContent() {
        return this.requestContent;
    }

    @ActiveReflection
    public Date getRemoveTime() {
        return this.removeTime;
    }

    @ActiveReflection
    public Customer getCustomer() {
        return this.customer;
    }

    @ActiveReflection
    public void setPathRequest(String pathRequest) {
        this.pathRequest = pathRequest;
    }

    @ActiveReflection
    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    @ActiveReflection
    public void setRequestContent(String requestContent) {
        this.requestContent = requestContent;
    }

    @ActiveReflection
    public void setRemoveTime(Date removeTime) {
        this.removeTime = removeTime;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
