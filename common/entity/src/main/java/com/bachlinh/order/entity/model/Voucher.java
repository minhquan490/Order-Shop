package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.annotation.Validator;
import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "VOUCHER")
@Label("VOU-")
@Validator(validators = "com.bachlinh.order.core.entity.validator.internal.VoucherValidator")
@ActiveReflection
public class Voucher extends AbstractEntity {

    @Id
    @Column(name = "ID", columnDefinition = "varchar(32)", nullable = false, updatable = false)
    private String id;

    @Column(name = "DISCOUNT_PERCENT", nullable = false)
    private Integer discountPercent;

    @Column(name = "TIME_START", nullable = false)
    private Timestamp timeStart;

    @Column(name = "TIME_EXPIRED", nullable = false)
    private Timestamp timeExpired;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "nvarchar(500)")
    private String voucherContent;

    @Column(name = "COST", nullable = false)
    private Integer voucherCost;

    @Column(name = "ENABLED", columnDefinition = "bit")
    private boolean isEnable = false;

    @ManyToMany(mappedBy = "assignedVouchers")
    private Set<Customer> customers;

    @ActiveReflection
    Voucher() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
        }
        throw new PersistenceException("Id of voucher must be string");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voucher voucher = (Voucher) o;
        return Objects.equal(getId(), voucher.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @ActiveReflection
    public String getId() {
        return this.id;
    }

    @ActiveReflection
    public Integer getDiscountPercent() {
        return this.discountPercent;
    }

    @ActiveReflection
    public Timestamp getTimeStart() {
        return this.timeStart;
    }

    @ActiveReflection
    public Timestamp getTimeExpired() {
        return this.timeExpired;
    }

    @ActiveReflection
    public String getVoucherContent() {
        return this.voucherContent;
    }

    @ActiveReflection
    public Integer getVoucherCost() {
        return this.voucherCost;
    }

    @ActiveReflection
    public boolean isEnable() {
        return this.isEnable;
    }

    @ActiveReflection
    public Set<Customer> getCustomers() {
        return this.customers;
    }

    @ActiveReflection
    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    @ActiveReflection
    public void setTimeStart(Timestamp timeStart) {
        this.timeStart = timeStart;
    }

    @ActiveReflection
    public void setTimeExpired(Timestamp timeExpired) {
        this.timeExpired = timeExpired;
    }

    @ActiveReflection
    public void setVoucherContent(String voucherContent) {
        this.voucherContent = voucherContent;
    }

    @ActiveReflection
    public void setVoucherCost(Integer voucherCost) {
        this.voucherCost = voucherCost;
    }

    @ActiveReflection
    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    @ActiveReflection
    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }
}