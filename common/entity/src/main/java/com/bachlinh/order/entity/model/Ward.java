package com.bachlinh.order.entity.model;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableFullTextSearch;
import com.bachlinh.order.core.annotation.FullTextField;
import com.bachlinh.order.core.annotation.QueryCache;
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
public class Ward extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", nullable = false, unique = true)
    private Integer id;

    @Column(name = "NAME", columnDefinition = "nvarchar(100)")
    @FullTextField
    private String name;

    @Column(name = "CODE")
    private Integer code;

    @Column(name = "CODE_NAME", length = 50)
    @FullTextField
    private String codeName;

    @Column(name = "DIVISION_TYPE", columnDefinition = "nvarchar(100)")
    private String divisionType;

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
    public void setName(String name) {
        if (this.name != null && !this.name.equals(name)) {
            trackUpdatedField("NAME", this.name, name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setCode(Integer code) {
        if (this.code != null && !this.code.equals(code)) {
            trackUpdatedField("CODE", this.code, code);
        }
        this.code = code;
    }

    @ActiveReflection
    public void setCodeName(String codeName) {
        if (this.codeName != null && !this.codeName.equals(codeName)) {
            trackUpdatedField("CODE_NAME", this.codeName, codeName);
        }
        this.codeName = codeName;
    }

    @ActiveReflection
    public void setDivisionType(String divisionType) {
        if (this.divisionType != null && !this.divisionType.equals(divisionType)) {
            trackUpdatedField("DIVISION_TYPE", this.divisionType, divisionType);
        }
        this.divisionType = divisionType;
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

    public String getName() {
        return this.name;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getCodeName() {
        return this.codeName;
    }

    public String getDivisionType() {
        return this.divisionType;
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
