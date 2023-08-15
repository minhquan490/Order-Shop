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
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;

@Label("ADR-")
@Entity
@Table(name = "ADDRESS", indexes = @Index(name = "idx_address_customer", columnList = "CUSTOMER_ID"))
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
@Getter
public class Address extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", nullable = false, columnDefinition = "varchar(32)", updatable = false, unique = true)
    private String id;

    @Column(name = "VALUE", nullable = false, columnDefinition = "nvarchar(266)")
    private String value;

    @Column(name = "CITY", nullable = false, columnDefinition = "nvarchar(266)")
    private String city;

    @Column(name = "COUNTRY", columnDefinition = "nvarchar(266)")
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    @Fetch(FetchMode.JOIN)
    private Customer customer;

    @Override
    @ActiveReflection
    public void setId(@NonNull Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Address entity must be string");
        }
        this.id = (String) id;
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
    public void setValue(@NonNull String value) {
        if (this.value != null && !value.equals(this.value)) {
            trackUpdatedField("VALUE", this.value);
        }
        this.value = value;
    }

    @ActiveReflection
    public void setCity(@NonNull String city) {
        if (this.city != null && !city.equals(this.city)) {
            trackUpdatedField("CITY", this.city);
        }
        this.city = city;
    }

    @ActiveReflection
    public void setCountry(@NonNull String country) {
        if (this.country != null && !country.equals(this.country)) {
            trackUpdatedField("COUNTRY", this.country);
        }
        this.country = country;
    }

    @ActiveReflection
    public void setCustomer(@NonNull Customer customer) {
        if (this.customer != null && !this.customer.getId().equals(customer.getId())) {
            trackUpdatedField("CUSTOMER_ID", this.customer.getId());
        }
        this.customer = customer;
    }

    public static EntityMapper<Address> getMapper() {
        return new AddressMapper();
    }

    private static class AddressMapper implements EntityMapper<Address> {

        @Override
        public Address map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new Address().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public Address map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            Address result = new Address();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().startsWith("ADDRESS")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Customer.getMapper();
                var customer = mapper.map(resultSet);
                result.setCustomer(customer);
            }
            return result;
        }

        private void setData(Address target, MappingObject mappingObject) {
            switch (mappingObject.columnName()) {
                case "ADDRESS.ID" -> target.setId(mappingObject.value());
                case "ADDRESS.VALUE" -> target.setValue((String) mappingObject.value());
                case "ADDRESS.CITY" -> target.setCity((String) mappingObject.value());
                case "ADDRESS.COUNTRY" -> target.setCountry((String) mappingObject.value());
                case "ADDRESS.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "ADDRESS.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "ADDRESS.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "ADDRESS.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
