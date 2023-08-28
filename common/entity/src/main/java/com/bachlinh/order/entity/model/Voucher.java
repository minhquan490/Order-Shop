package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
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
@DynamicUpdate
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
    private boolean active = false;

    @ManyToMany(mappedBy = "assignedVouchers")
    @EqualsAndHashCode.Exclude
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

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
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
            trackUpdatedField("NAME", this.name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setDiscountPercent(Integer discountPercent) {
        if (this.discountPercent != null && !this.discountPercent.equals(discountPercent)) {
            trackUpdatedField("DISCOUNT_PERCENT", this.discountPercent.toString());
        }
        this.discountPercent = discountPercent;
    }

    @ActiveReflection
    public void setTimeStart(Timestamp timeStart) {
        if (this.timeStart != null && !this.timeStart.equals(timeStart)) {
            trackUpdatedField("TIME_START", this.timeStart.toString());
        }
        this.timeStart = timeStart;
    }

    @ActiveReflection
    public void setTimeExpired(Timestamp timeExpired) {
        if (this.timeExpired != null && !this.timeExpired.equals(timeExpired)) {
            trackUpdatedField("TIME_EXPIRED", this.timeExpired.toString());
        }
        this.timeExpired = timeExpired;
    }

    @ActiveReflection
    public void setVoucherContent(String voucherContent) {
        if (this.voucherContent != null && !this.voucherContent.equals(voucherContent)) {
            trackUpdatedField("CONTENT", this.voucherContent);
        }
        this.voucherContent = voucherContent;
    }

    @ActiveReflection
    public void setVoucherCost(Integer voucherCost) {
        if (this.voucherCost != null && !this.voucherCost.equals(voucherCost)) {
            trackUpdatedField("COST", this.voucherCost.toString());
        }
        this.voucherCost = voucherCost;
    }

    @ActiveReflection
    public void setActive(boolean isEnable) {
        if (this.active != isEnable) {
            trackUpdatedField("ENABLED", String.valueOf(this.active));
        }
        this.active = isEnable;
    }

    @ActiveReflection
    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    public static EntityMapper<Voucher> getMapper() {
        return new VoucherMapper();
    }

    private static class VoucherMapper implements EntityMapper<Voucher> {

        @Override
        public Voucher map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new Voucher().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public Voucher map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            Voucher result = new Voucher();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("VOUCHER")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Customer.getMapper();
                Set<Customer> customerSet = new LinkedHashSet<>();
                while (!resultSet.isEmpty()) {
                    hook = resultSet.peek();
                    if (hook.columnName().split("\\.")[0].equals("CUSTOMER")) {
                        var customer = mapper.map(resultSet);
                        result.getCustomers().add(customer);
                        customerSet.add(customer);
                    }
                }
                result.setCustomers(customerSet);
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("VOUCHER");
            });
        }

        private void setData(Voucher target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "VOUCHER.ID" -> target.setId(mappingObject.value());
                case "VOUCHER.NAME" -> target.setName((String) mappingObject.value());
                case "VOUCHER.DISCOUNT_PERCENT" -> target.setDiscountPercent((Integer) mappingObject.value());
                case "VOUCHER.TIME_START" -> target.setTimeStart((Timestamp) mappingObject.value());
                case "VOUCHER.TIME_EXPIRED" -> target.setTimeExpired((Timestamp) mappingObject.value());
                case "VOUCHER.CONTENT" -> target.setVoucherContent((String) mappingObject.value());
                case "VOUCHER.COST" -> target.setVoucherCost((Integer) mappingObject.value());
                case "VOUCHER.ENABLED" -> target.setActive((Boolean) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}