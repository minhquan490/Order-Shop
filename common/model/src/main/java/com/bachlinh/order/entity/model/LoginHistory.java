package com.bachlinh.order.entity.model;

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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(name = "LOGIN_HISTORY", indexes = @Index(name = "idx_login_history_customer", columnList = "CUSTOMER_ID"))
@ActiveReflection
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
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

    @ActiveReflection
    protected LoginHistory() {
    }

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
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setLastLoginTime(Timestamp lastLoginTime) {
        if (this.lastLoginTime != null && !Objects.equal(lastLoginTime, this.lastLoginTime)) {
            trackUpdatedField("LAST_LOGIN_TIME", this.lastLoginTime, lastLoginTime);
        }
        this.lastLoginTime = lastLoginTime;
    }

    @ActiveReflection
    public void setLoginIp(String loginIp) {
        if (this.loginIp != null && !Objects.equal(loginIp, this.loginIp)) {
            trackUpdatedField("LOGIN_IP", this.loginIp, loginIp);
        }
        this.loginIp = loginIp;
    }

    @ActiveReflection
    public void setSuccess(Boolean success) {
        if (this.success != null && !Objects.equal(success, this.success)) {
            trackUpdatedField("SUCCESS", this.success, success);
        }
        this.success = success;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getId() {
        return this.id;
    }

    public Timestamp getLastLoginTime() {
        return this.lastLoginTime;
    }

    public String getLoginIp() {
        return this.loginIp;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginHistory that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getLastLoginTime(), that.getLastLoginTime()) && Objects.equal(getLoginIp(), that.getLoginIp()) && Objects.equal(getSuccess(), that.getSuccess()) && Objects.equal(getCustomer(), that.getCustomer());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getId(), getLastLoginTime(), getLoginIp(), getSuccess(), getCustomer());
    }
}