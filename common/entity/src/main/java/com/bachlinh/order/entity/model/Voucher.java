package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.annotation.Trigger;
import com.bachlinh.order.annotation.Validator;
import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "VOUCHER",
        indexes = @Index(name = "idx_voucher_name", columnList = "NAME")
)
@Label("VOU-")
@Validator(validators = "com.bachlinh.order.validate.validator.internal.VoucherValidator")
@ActiveReflection
@EnableFullTextSearch
@Trigger(triggers = {"com.bachlinh.order.trigger.internal.VoucherIndexTrigger"})
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
public class Voucher extends AbstractEntity {

    @Id
    @Column(name = "ID", columnDefinition = "varchar(32)", nullable = false, updatable = false)
    private String id;

    @Column(name = "NAME", nullable = false, unique = true)
    @FullTextField
    @ActiveReflection
    private String name;

    @Column(name = "DISCOUNT_PERCENT", nullable = false)
    private Integer discountPercent;

    @Column(name = "TIME_START", nullable = false)
    private Timestamp timeStart;

    @Column(name = "TIME_EXPIRED", nullable = false)
    private Timestamp timeExpired;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "nvarchar(500)")
    @FullTextField
    @ActiveReflection
    private String voucherContent;

    @Column(name = "COST", nullable = false)
    private Integer voucherCost;

    @Column(name = "ENABLED", columnDefinition = "bit")
    private boolean active = false;

    @ManyToMany(mappedBy = "assignedVouchers")
    private Set<Customer> customers = new HashSet<>();

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of voucher must be string");
    }

    @ActiveReflection
    public void setName(String name) {
        this.name = name;
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
    public void setActive(boolean isEnable) {
        this.active = isEnable;
    }

    @ActiveReflection
    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Voucher voucher)) return false;
        return isActive() == voucher.isActive() && Objects.equal(getId(), voucher.getId()) && Objects.equal(getName(), voucher.getName()) && Objects.equal(getDiscountPercent(), voucher.getDiscountPercent()) && Objects.equal(getTimeStart(), voucher.getTimeStart()) && Objects.equal(getTimeExpired(), voucher.getTimeExpired()) && Objects.equal(getVoucherContent(), voucher.getVoucherContent()) && Objects.equal(getVoucherCost(), voucher.getVoucherCost()) && Objects.equal(getCustomers(), voucher.getCustomers());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getName(), getDiscountPercent(), getTimeStart(), getTimeExpired(), getVoucherContent(), getVoucherCost(), isActive(), getCustomers());
    }
}