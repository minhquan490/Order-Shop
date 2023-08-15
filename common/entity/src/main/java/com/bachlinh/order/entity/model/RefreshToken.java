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
import jakarta.persistence.OneToOne;
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
@Table(name = "REFRESH_TOKEN", indexes = @Index(name = "idx_token_value", columnList = "VALUE"))
@Label("RFT-")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class RefreshToken extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", unique = true, nullable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "TIME_CREATED", nullable = false)
    private Timestamp timeCreated;

    @Column(name = "TIME_EXPIRED", nullable = false)
    private Timestamp timeExpired;

    @Column(name = "VALUE", nullable = false, length = 100)
    private String refreshTokenValue;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", unique = true, nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer customer;

    @ActiveReflection
    @Override
    public void setId(Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Id of refresh token is only string");
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
    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    @ActiveReflection
    public void setTimeExpired(Timestamp timeExpired) {
        this.timeExpired = timeExpired;
    }

    @ActiveReflection
    public void setRefreshTokenValue(String refreshTokenValue) {
        this.refreshTokenValue = refreshTokenValue;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public static EntityMapper<RefreshToken> getMapper() {
        return new RefreshTokenMapper();
    }

    private static class RefreshTokenMapper implements EntityMapper<RefreshToken> {

        @Override
        public RefreshToken map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new RefreshToken().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public RefreshToken map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            RefreshToken result = new RefreshToken();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().startsWith("REFRESH_TOKEN")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Customer.getMapper();
                var customer = mapper.map(resultSet);
                customer.setRefreshToken(result);
                result.setCustomer(customer);
            }
            return result;
        }

        private void setData(RefreshToken target, MappingObject mappingObject) {
            switch (mappingObject.columnName()) {
                case "REFRESH_TOKEN.ID" -> target.setId(mappingObject.value());
                case "REFRESH_TOKEN.TIME_CREATED" -> target.setTimeCreated((Timestamp) mappingObject.value());
                case "REFRESH_TOKEN.TIME_EXPIRED" -> target.setTimeExpired((Timestamp) mappingObject.value());
                case "REFRESH_TOKEN.VALUE" -> target.setRefreshTokenValue((String) mappingObject.value());
                case "REFRESH_TOKEN.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "REFRESH_TOKEN.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "REFRESH_TOKEN.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "REFRESH_TOKEN.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
