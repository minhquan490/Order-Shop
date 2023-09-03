package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;

@Entity
@Table(name = "TEMPORARY_TOKEN", indexes = @Index(name = "idx_temporary_token_value", columnList = "VALUE"))
@Getter
@ActiveReflection
@Label("TPT-")
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__(@ActiveReflection))
@EqualsAndHashCode(callSuper = true)
public class TemporaryToken extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", unique = true, updatable = false, nullable = false, columnDefinition = "varchar(32)")
    private Integer id;

    @Column(name = "VALUE", unique = true, nullable = false)
    private String value;

    @Column(name = "EXPIRY_TIME", nullable = false)
    private Timestamp expiryTime;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "temporaryToken")
    @EqualsAndHashCode.Exclude
    private Customer assignCustomer;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of Temporary token must be integer");
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
    public void setValue(String value) {
        if (this.value != null && !this.value.equals(value)) {
            trackUpdatedField("VALUE", this.value, value);
        }
        this.value = value;
    }

    @ActiveReflection
    public void setExpiryTime(Timestamp expiryTime) {
        if (this.expiryTime != null && !this.expiryTime.equals(expiryTime)) {
            trackUpdatedField("EXPIRY_TIME", this.expiryTime, expiryTime);
        }
        this.expiryTime = expiryTime;
    }

    @ActiveReflection
    public void setAssignCustomer(Customer assignCustomer) {
        this.assignCustomer = assignCustomer;
    }

    public static EntityMapper<TemporaryToken> getMapper() {
        return new TemporaryTokenMapper();
    }

    private static class TemporaryTokenMapper implements EntityMapper<TemporaryToken> {

        @Override
        public TemporaryToken map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new TemporaryToken().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public TemporaryToken map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            TemporaryToken result = new TemporaryToken();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("TEMPORARY_TOKEN")) {
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
                    customer.setTemporaryToken(result);
                    result.setAssignCustomer(customer);
                }
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("TEMPORARY_TOKEN");
            });
        }

        private void setData(TemporaryToken target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "TEMPORARY_TOKEN.ID" -> target.setId(mappingObject.value());
                case "TEMPORARY_TOKEN.VALUE" -> target.setValue((String) mappingObject.value());
                case "TEMPORARY_TOKEN.EXPIRY_TIME" -> target.setExpiryTime((Timestamp) mappingObject.value());
                case "TEMPORARY_TOKEN.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "TEMPORARY_TOKEN.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "TEMPORARY_TOKEN.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "TEMPORARY_TOKEN.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
