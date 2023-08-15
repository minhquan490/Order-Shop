package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.NonNull;

import java.sql.Date;
import java.util.Collection;
import java.util.Queue;

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
    @Fetch(FetchMode.JOIN)
    private Customer customer;

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
    public <U extends BaseEntity<Integer>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
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

    public static EntityMapper<CustomerAccessHistory> getMapper() {
        return new CustomerAccessHistoryMapper();
    }

    private static class CustomerAccessHistoryMapper implements EntityMapper<CustomerAccessHistory> {

        @Override
        public CustomerAccessHistory map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new CustomerAccessHistory().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public CustomerAccessHistory map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            CustomerAccessHistory result = new CustomerAccessHistory();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().startsWith("CUSTOMER_ACCESS_HISTORY")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Customer.getMapper();
                var customer = mapper.map(resultSet);
                customer.getHistories().add(result);
                result.setCustomer(customer);
            }
            return result;
        }

        private void setData(CustomerAccessHistory target, MappingObject mappingObject) {
            switch (mappingObject.columnName()) {
                case "CUSTOMER_ACCESS_HISTORY.ID" -> target.setId(mappingObject.value());
                case "CUSTOMER_ACCESS_HISTORY.PATH_REQUEST" -> target.setPathRequest((String) mappingObject.value());
                case "CUSTOMER_ACCESS_HISTORY.REQUEST_TYPE" -> target.setRequestType((String) mappingObject.value());
                case "CUSTOMER_ACCESS_HISTORY.REQUEST_TIME" -> target.setRequestTime((Date) mappingObject.value());
                case "CUSTOMER_ACCESS_HISTORY.REQUEST_CONTENT" ->
                        target.setRequestContent((String) mappingObject.value());
                case "CUSTOMER_ACCESS_HISTORY.CUSTOMER_IP" -> target.setCustomerIp((String) mappingObject.value());
                case "CUSTOMER_ACCESS_HISTORY.REMOVED_TIME" -> target.setRemoveTime((Date) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
