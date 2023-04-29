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
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.List;

@Entity
@Table(
        name = "PROVINCE",
        indexes = {
                @Index(name = "idx_province_name", columnList = "NAME", unique = true),
                @Index(name = "idx_province_code", columnList = "CODE", unique = true)
        }
)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "province")
@EnableFullTextSearch
@Trigger(triggers = "com.bachlinh.order.core.entity.trigger.internal.ProvinceIndexTrigger")
@ActiveReflection
public class Province extends AbstractEntity {

    @Id
    @Column(name = "ID", nullable = false, unique = true)
    private Integer id;

    @Column(name = "NAME", columnDefinition = "nvarchar(100)")
    @FullTextField
    private String name;

    @Column(name = "CODE")
    private Integer code;

    @Column(name = "DIVISION_TYPE", columnDefinition = "nvarchar(100)")
    private String divisionType;

    @Column(name = "CODE_NAME", length = 50)
    @FullTextField
    private String codeName;

    @Column(name = "PHONE_CODE")
    private Integer phoneCode;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "province")
    private List<District> districts;

    @ActiveReflection
    Province() {
    }

    @Override
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of province must be integer");
        }
        this.id = (Integer) id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Province province = (Province) o;
        return Objects.equal(getId(), province.getId()) && Objects.equal(getName(), province.getName()) && Objects.equal(getCode(), province.getCode()) && Objects.equal(getDivisionType(), province.getDivisionType()) && Objects.equal(getCodeName(), province.getCodeName()) && Objects.equal(getPhoneCode(), province.getPhoneCode());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getName(), getCode(), getDivisionType(), getCodeName(), getPhoneCode());
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
    public String getDivisionType() {
        return this.divisionType;
    }

    @ActiveReflection
    public String getCodeName() {
        return this.codeName;
    }

    @ActiveReflection
    public Integer getPhoneCode() {
        return this.phoneCode;
    }

    @ActiveReflection
    public List<District> getDistricts() {
        return this.districts;
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
    public void setDivisionType(String divisionType) {
        this.divisionType = divisionType;
    }

    @ActiveReflection
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    @ActiveReflection
    public void setPhoneCode(Integer phoneCode) {
        this.phoneCode = phoneCode;
    }

    @ActiveReflection
    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }
}
