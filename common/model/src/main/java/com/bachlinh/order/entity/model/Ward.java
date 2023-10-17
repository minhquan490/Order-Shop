package com.bachlinh.order.entity.model;

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
import com.bachlinh.order.core.annotation.EnableFullTextSearch;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.core.annotation.QueryCache;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(
        name = "WARD",
        indexes = {
                @Index(name = "idx_ward_name", columnList = "NAME"),
                @Index(name = "idx_ward_code", columnList = "CODE")
        }
)
@ActiveReflection
@EnableFullTextSearch
@QueryCache
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
public class Ward extends VnAddress<Integer> {

    @Id
    @Column(name = "ID", nullable = false, unique = true)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "DISTRICT_ID", nullable = false)
    @Fetch(FetchMode.JOIN)
    private District district;

    @ActiveReflection
    protected Ward() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of ward must be int");
        }
        this.id = (Integer) id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setDistrict(District district) {
        if (this.district != null && !Objects.requireNonNull(this.district.getId()).equals(district.getId())) {
            trackUpdatedField("DISTRICT_ID", this.district.getId(), district.getId());
        }
        this.district = district;
    }

    public Integer getId() {
        return this.id;
    }

    public District getDistrict() {
        return this.district;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ward ward)) return false;
        if (!super.equals(o)) return false;
        return com.google.common.base.Objects.equal(getId(), ward.getId()) && com.google.common.base.Objects.equal(getName(), ward.getName()) && com.google.common.base.Objects.equal(getCode(), ward.getCode()) && com.google.common.base.Objects.equal(getCodeName(), ward.getCodeName()) && com.google.common.base.Objects.equal(getDivisionType(), ward.getDivisionType()) && com.google.common.base.Objects.equal(getDistrict(), ward.getDistrict());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(super.hashCode(), getId(), getName(), getCode(), getCodeName(), getDivisionType(), getDistrict());
    }
}
