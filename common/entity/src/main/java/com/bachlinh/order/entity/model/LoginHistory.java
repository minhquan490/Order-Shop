package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Validator;
import com.google.common.base.Objects;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "LOGIN_HISTORY", indexes = @Index(name = "idx_login_history_customer", columnList = "CUSTOMER_ID"))
@ActiveReflection
@Validator(validators = "com.bachlinh.order.validate.validator.internal.LoginHistoryValidator")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
public class LoginHistory extends AbstractEntity {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, columnDefinition = "int")
    private Integer id;

    @Column(name = "LAST_LOGIN_TIME", nullable = false)
    private Timestamp lastLoginTime;

    @Column(name = "LOGIN_IP", nullable = false, length = 30)
    private String loginIp;

    @Column(name = "SUCCESS", columnDefinition = "bit", nullable = false)
    private Boolean success;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false)
    private Customer customer;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of login history must be int");
        }
        this.id = (Integer) id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginHistory that)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getLastLoginTime(), that.getLastLoginTime()) && Objects.equal(getLoginIp(), that.getLoginIp()) && Objects.equal(getSuccess(), that.getSuccess()) && Objects.equal(getCustomer(), that.getCustomer());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getLastLoginTime(), getLoginIp(), getSuccess(), getCustomer());
    }
}