package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.NonNull;

import java.sql.Date;

@Entity
@Table(name = "CUSTOMER_ACCESS_HISTORY", indexes = @Index(name = "idx_access_history_customer", columnList = "CUSTOMER_ID"))
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class CustomerAccessHistory extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, columnDefinition = "int")
    @NonNull
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
    private Customer customer;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of history must be int");
        }
        this.id = (Integer) id;
    }

    @ActiveReflection
    public void setPathRequest(String pathRequest) {
        if (this.pathRequest != null && !this.pathRequest.equals(pathRequest)) {
            trackUpdatedField("PATH_REQUEST", this.pathRequest);
        }
        this.pathRequest = pathRequest;
    }

    @ActiveReflection
    public void setRequestTime(Date requestTime) {
        if (this.requestTime != null && !this.requestTime.equals(requestTime)) {
            trackUpdatedField("REQUEST_TIME", this.requestTime.toString());
        }
        this.requestTime = requestTime;
    }

    @ActiveReflection
    public void setRequestContent(String requestContent) {
        if (this.requestContent != null && !this.requestContent.equals(requestContent)) {
            trackUpdatedField("REQUEST_CONTENT", this.requestContent);
        }
        this.requestContent = requestContent;
    }

    @ActiveReflection
    public void setRemoveTime(Date removeTime) {
        if (this.removeTime != null && !this.removeTime.equals(removeTime)) {
            trackUpdatedField("REMOVED_TIME", this.removeTime.toString());
        }
        this.removeTime = removeTime;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @ActiveReflection
    public void setRequestType(String requestType) {
        if (this.requestType != null && !this.requestType.equals(requestType)) {
            trackUpdatedField("REQUEST_TYPE", this.requestType);
        }
        this.requestType = requestType;
    }

    @ActiveReflection
    public void setCustomerIp(String customerIp) {
        if (this.customerIp != null && !this.customerIp.equals(customerIp)) {
            trackUpdatedField("CUSTOMER_IP", this.customerIp);
        }
        this.customerIp = customerIp;
    }
}
