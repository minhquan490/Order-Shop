package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;

@Label("CMA-")
@Table(name = "CUSTOMER_MEDIA")
@Entity
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
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
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @ActiveReflection
    public void setContentLength(long contentLength) {
        if (this.contentLength != null && !this.contentLength.equals(contentLength)) {
            trackUpdatedField("CONTENT_LENGTH", this.contentLength.toString());
        }
        this.contentLength = contentLength;
    }

    @ActiveReflection
    public void setUrl(String url) {
        if (this.url != null && !this.url.equals(url)) {
            trackUpdatedField("URL", this.url);
        }
        this.url = url;
    }

    @ActiveReflection
    public void setContentType(String contentType) {
        if (this.contentType != null && !this.contentType.equals(contentType)) {
            trackUpdatedField("CONTENT_TYPE", this.contentType);
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
    public <U extends BaseEntity<String>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    public static EntityMapper<CustomerMedia> getMapper() {
        return new CustomerMediaMapper();
    }

    private static class CustomerMediaMapper implements EntityMapper<CustomerMedia> {

        @Override
        public CustomerMedia map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new CustomerMedia().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public CustomerMedia map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            CustomerMedia result = new CustomerMedia();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("CUSTOMER_MEDIA")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Customer.getMapper();
                if (mapper.canMap(resultSet)) {
                    var customer = mapper.map(resultSet);
                    customer.setCustomerMedia(result);
                    result.setCustomer(customer);
                }
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("CUSTOMER_MEDIA");
            });
        }

        private void setData(CustomerMedia target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "CUSTOMER_MEDIA.ID" -> target.setId(mappingObject.value());
                case "CUSTOMER_MEDIA.URL" -> target.setUrl((String) mappingObject.value());
                case "CUSTOMER_MEDIA.CONTENT_TYPE" -> target.setContentType((String) mappingObject.value());
                case "CUSTOMER_MEDIA.CONTENT_LENGTH" -> target.setContentLength((Long) mappingObject.value());
                case "CUSTOMER_MEDIA.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "CUSTOMER_MEDIA.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "CUSTOMER_MEDIA.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "CUSTOMER_MEDIA.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
