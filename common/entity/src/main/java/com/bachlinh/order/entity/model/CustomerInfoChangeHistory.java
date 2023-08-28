package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;

@Entity
@Table(name = "CUSTOMER_INFORMATION_CHANGE_HISTORY", indexes = @Index(name = "idx_customer_history_id", columnList = "CUSTOMER_ID"))
@Getter
@ActiveReflection
@Label("CIH-")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class CustomerInfoChangeHistory extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", updatable = false, unique = true, nullable = false)
    private String id;

    @Column(name = "OLD_VALUE", updatable = false, nullable = false)
    private String oldValue;

    @Column(name = "FIELD_NAME", updatable = false, nullable = false)
    private String fieldName;

    @Column(name = "TIME_UPDATE", updatable = false, nullable = false)
    private Timestamp timeUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of CustomerInfoChange must be string");
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

    @ActiveReflection
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    @ActiveReflection
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @ActiveReflection
    public void setTimeUpdate(Timestamp timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public static EntityMapper<CustomerInfoChangeHistory> getMapper() {
        return new CustomerInfoChangeHistoryMapper();
    }

    private static class CustomerInfoChangeHistoryMapper implements EntityMapper<CustomerInfoChangeHistory> {

        @Override
        public CustomerInfoChangeHistory map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new CustomerInfoChangeHistory().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public CustomerInfoChangeHistory map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            CustomerInfoChangeHistory result = new CustomerInfoChangeHistory();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("CUSTOMER_INFORMATION_CHANGE_HISTORY")) {
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
                    result.setCustomer(customer);
                }
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("CUSTOMER_INFORMATION_CHANGE_HISTORY");
            });
        }

        private void setData(CustomerInfoChangeHistory target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "CUSTOMER_INFORMATION_CHANGE_HISTORY.ID" -> target.setId(mappingObject.value());
                case "CUSTOMER_INFORMATION_CHANGE_HISTORY.OLD_VALUE" ->
                        target.setOldValue((String) mappingObject.value());
                case "CUSTOMER_INFORMATION_CHANGE_HISTORY.FIELD_NAME" ->
                        target.setFieldName((String) mappingObject.value());
                case "CUSTOMER_INFORMATION_CHANGE_HISTORY.TIME_UPDATE" ->
                        target.setTimeUpdate((Timestamp) mappingObject.value());
                case "CUSTOMER_INFORMATION_CHANGE_HISTORY.CREATED_BY" ->
                        target.setCreatedBy((String) mappingObject.value());
                case "CUSTOMER_INFORMATION_CHANGE_HISTORY.MODIFIED_BY" ->
                        target.setModifiedBy((String) mappingObject.value());
                case "CUSTOMER_INFORMATION_CHANGE_HISTORY.CREATED_DATE" ->
                        target.setCreatedDate((Timestamp) mappingObject.value());
                case "CUSTOMER_INFORMATION_CHANGE_HISTORY.MODIFIED_DATE" ->
                        target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
