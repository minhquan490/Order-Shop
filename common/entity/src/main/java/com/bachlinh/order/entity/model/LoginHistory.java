package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.CascadeType;
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
@Table(name = "LOGIN_HISTORY", indexes = @Index(name = "idx_login_history_customer", columnList = "CUSTOMER_ID"))
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class LoginHistory extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, columnDefinition = "int")
    private Integer id;

    @Column(name = "LAST_LOGIN_TIME", nullable = false)
    private Timestamp lastLoginTime;

    @Column(name = "LOGIN_IP", nullable = false, length = 30)
    private String loginIp;

    @Column(name = "SUCCESS", columnDefinition = "bit", nullable = false)
    private Boolean success;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer customer;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of login history must be int");
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
    public void setLastLoginTime(Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @ActiveReflection
    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    @ActiveReflection
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public static EntityMapper<LoginHistory> getMapper() {
        return new LoginHistoryMapper();
    }

    private static class LoginHistoryMapper implements EntityMapper<LoginHistory> {

        @Override
        public LoginHistory map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new LoginHistory().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public LoginHistory map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            LoginHistory result = new LoginHistory();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().startsWith("LOGIN_HISTORY")) {
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

        private void setData(LoginHistory target, MappingObject mappingObject) {
            switch (mappingObject.columnName()) {
                case "LOGIN_HISTORY.ID" -> target.setId(mappingObject.value());
                case "LOGIN_HISTORY.LAST_LOGIN_TIME" -> target.setLastLoginTime((Timestamp) mappingObject.value());
                case "LOGIN_HISTORY.LOGIN_IP" -> target.setLoginIp((String) mappingObject.value());
                case "LOGIN_HISTORY.SUCCESS" -> target.setSuccess((Boolean) mappingObject.value());
                case "LOGIN_HISTORY.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "LOGIN_HISTORY.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "LOGIN_HISTORY.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "LOGIN_HISTORY.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}