package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Label;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

@Entity
@Table(
        name = "VOUCHER",
        indexes = @Index(name = "idx_voucher_name", columnList = "NAME")
)
@Label("VOU-")
@ActiveReflection
@EnableFullTextSearch
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = true)
public class Voucher extends AbstractEntity<String> {

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
    private Boolean active;

    @ManyToMany(mappedBy = "assignedVouchers")
    @EqualsAndHashCode.Exclude
    private Set<Customer> customers = new HashSet<>();

    public boolean isActive() {
        return getActive();
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of voucher must be string");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        } else {
            Deque<Voucher> results = new LinkedList<>();
            Voucher first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (Voucher) entity;
                } else {
                    Voucher casted = (Voucher) entity;
                    if (casted.getCustomers().isEmpty()) {
                        results.add(casted);
                    } else {
                        first.getCustomers().addAll(casted.getCustomers());
                    }
                }
            }
            results.addFirst(first);
            return (Collection<U>) results;
        }
    }

    @ActiveReflection
    public void setName(String name) {
        if (this.name != null && !this.name.equals(name)) {
            trackUpdatedField("NAME", this.name, name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setDiscountPercent(Integer discountPercent) {
        if (this.discountPercent != null && !this.discountPercent.equals(discountPercent)) {
            trackUpdatedField("DISCOUNT_PERCENT", this.discountPercent, discountPercent);
        }
        this.discountPercent = discountPercent;
    }

    @ActiveReflection
    public void setTimeStart(Timestamp timeStart) {
        if (this.timeStart != null && !this.timeStart.equals(timeStart)) {
            trackUpdatedField("TIME_START", this.timeStart, timeStart);
        }
        this.timeStart = timeStart;
    }

    @ActiveReflection
    public void setTimeExpired(Timestamp timeExpired) {
        if (this.timeExpired != null && !this.timeExpired.equals(timeExpired)) {
            trackUpdatedField("TIME_EXPIRED", this.timeExpired, timeExpired);
        }
        this.timeExpired = timeExpired;
    }

    @ActiveReflection
    public void setVoucherContent(String voucherContent) {
        if (this.voucherContent != null && !this.voucherContent.equals(voucherContent)) {
            trackUpdatedField("CONTENT", this.voucherContent, voucherContent);
        }
        this.voucherContent = voucherContent;
    }

    @ActiveReflection
    public void setVoucherCost(Integer voucherCost) {
        if (this.voucherCost != null && !this.voucherCost.equals(voucherCost)) {
            trackUpdatedField("COST", this.voucherCost, voucherCost);
        }
        this.voucherCost = voucherCost;
    }

    @ActiveReflection
    public void setActive(Boolean isEnable) {
        if (this.active != null && this.active.equals(isEnable)) {
            trackUpdatedField("ENABLED", this.active, active);
        }
        this.active = isEnable;
    }

    @ActiveReflection
    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }
}