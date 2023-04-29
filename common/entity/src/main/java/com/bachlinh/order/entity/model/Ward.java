package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Trigger;
import com.google.common.base.Objects;
import jakarta.persistence.Cacheable;
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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(
        name = "WARD",
        indexes = {
                @Index(name = "idx_ward_name", columnList = "NAME"),
                @Index(name = "idx_ward_code", columnList = "CODE")
        }
)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ward")
@EnableFullTextSearch
@Trigger(triggers = "com.bachlinh.order.core.entity.trigger.internal.WardIndexTrigger")
@ActiveReflection
public class Ward extends AbstractEntity {

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
    private District district;

    @ActiveReflection
    Ward() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ward ward)) return false;
        return Objects.equal(getId(), ward.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @ActiveReflection
    public Integer getId() {
        return this.id;
    }

    @ActiveReflection
    public String getName() {
        return this.name;
    }

    @ActiveReflection
    public Integer getCode() {
        return this.code;
    }

    @ActiveReflection
    public String getCodeName() {
        return this.codeName;
    }

    @ActiveReflection
    public String getDivisionType() {
        return this.divisionType;
    }

    @ActiveReflection
    public District getDistrict() {
        return this.district;
    }

    @ActiveReflection
    public void setName(String name) {
        this.name = name;
    }

    @ActiveReflection
    public void setCode(Integer code) {
        this.code = code;
    }

    @ActiveReflection
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    @ActiveReflection
    public void setDivisionType(String divisionType) {
        this.divisionType = divisionType;
    }

    @ActiveReflection
    public void setDistrict(District district) {
        this.district = district;
    }
}
