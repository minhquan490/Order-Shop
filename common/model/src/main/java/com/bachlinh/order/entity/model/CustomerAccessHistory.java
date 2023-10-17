package com.bachlinh.order.entity.model;

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

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;

import java.sql.Date;
import java.util.Collection;
import java.util.Objects;

import org.springframework.lang.NonNull;

@Entity
@Table(name = "CUSTOMER_ACCESS_HISTORY", indexes = @Index(name = "idx_access_history_customer", columnList = "CUSTOMER_ID"))
@ActiveReflection
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
public class CustomerAccessHistory extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, columnDefinition = "int")
    private Integer id;

    @Column(name = "PATH_REQUEST", length = 50, nullable = false, updatable = false)
    private String pathRequest;

    // Use RequestType enum
    @Column(name = "REQUEST_TYPE", length = 15, nullable = false, updatable = false)
    private String requestType;

    @Column(name = "REQUEST_TIME", nullable = false, updatable = false)
    private Date requestTime;

    @Column(name = "REQUEST_CONTENT", columnDefinition = "nvarchar(400)")
    private String requestContent;

    @Column(name = "CUSTOMER_IP", length = 100)
    private String customerIp;

    @Column(name = "REMOVED_TIME", nullable = false)
    private Date removeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer customer;

    @ActiveReflection
    protected CustomerAccessHistory() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of history must be int");
        }
        this.id = (Integer) id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setPathRequest(String pathRequest) {
        if (this.pathRequest != null && !this.pathRequest.equals(pathRequest)) {
            trackUpdatedField("PATH_REQUEST", this.pathRequest, pathRequest);
        }
        this.pathRequest = pathRequest;
    }

    @ActiveReflection
    public void setRequestTime(Date requestTime) {
        if (this.requestTime != null && !this.requestTime.equals(requestTime)) {
            trackUpdatedField("REQUEST_TIME", this.requestTime, requestTime);
        }
        this.requestTime = requestTime;
    }

    @ActiveReflection
    public void setRequestContent(String requestContent) {
        if (this.requestContent != null && !this.requestContent.equals(requestContent)) {
            trackUpdatedField("REQUEST_CONTENT", this.requestContent, requestContent);
        }
        this.requestContent = requestContent;
    }

    @ActiveReflection
    public void setRemoveTime(Date removeTime) {
        if (this.removeTime != null && !this.removeTime.equals(removeTime)) {
            trackUpdatedField("REMOVED_TIME", this.removeTime, removeTime);
        }
        this.removeTime = removeTime;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        if (this.customer != null && this.customer.getId().equals(Objects.requireNonNull(customer).getId())) {
            trackUpdatedField("CUSTOMER_ID", this.customer.getId(), customer.getId());
        }
        this.customer = customer;
    }

    @ActiveReflection
    public void setRequestType(String requestType) {
        if (this.requestType != null && !this.requestType.equals(requestType)) {
            trackUpdatedField("REQUEST_TYPE", this.requestType, requestType);
        }
        this.requestType = requestType;
    }

    @ActiveReflection
    public void setCustomerIp(String customerIp) {
        if (this.customerIp != null && !this.customerIp.equals(customerIp)) {
            trackUpdatedField("CUSTOMER_IP", this.customerIp, customerIp);
        }
        this.customerIp = customerIp;
    }

    @NonNull
    public Integer getId() {
        return this.id;
    }

    public String getPathRequest() {
        return this.pathRequest;
    }

    public String getRequestType() {
        return this.requestType;
    }

    public Date getRequestTime() {
        return this.requestTime;
    }

    public String getRequestContent() {
        return this.requestContent;
    }

    public String getCustomerIp() {
        return this.customerIp;
    }

    public Date getRemoveTime() {
        return this.removeTime;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerAccessHistory that)) return false;
        if (!super.equals(o)) return false;
        return com.google.common.base.Objects.equal(getId(), that.getId()) && com.google.common.base.Objects.equal(getPathRequest(), that.getPathRequest()) && com.google.common.base.Objects.equal(getRequestType(), that.getRequestType()) && com.google.common.base.Objects.equal(getRequestTime(), that.getRequestTime()) && com.google.common.base.Objects.equal(getRequestContent(), that.getRequestContent()) && com.google.common.base.Objects.equal(getCustomerIp(), that.getCustomerIp()) && com.google.common.base.Objects.equal(getRemoveTime(), that.getRemoveTime()) && com.google.common.base.Objects.equal(getCustomer(), that.getCustomer());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(super.hashCode(), getId(), getPathRequest(), getRequestType(), getRequestTime(), getRequestContent(), getCustomerIp(), getRemoveTime(), getCustomer());
    }
}
