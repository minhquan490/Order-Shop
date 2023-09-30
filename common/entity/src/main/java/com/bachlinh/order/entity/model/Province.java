package com.bachlinh.order.entity.model;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableFullTextSearch;
import com.bachlinh.order.core.annotation.FullTextField;
import com.bachlinh.order.core.annotation.QueryCache;
import com.google.common.base.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(
        name = "PROVINCE",
        indexes = {
                @Index(name = "idx_province_name", columnList = "NAME", unique = true),
                @Index(name = "idx_province_code", columnList = "CODE", unique = true)
        }
)
@EnableFullTextSearch
@ActiveReflection
@QueryCache
public class Province extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", nullable = false, unique = true)
    private Integer id;

    @Column(name = "NAME", columnDefinition = "nvarchar(100)")
    @FullTextField
    @ActiveReflection
    private String name;

    @Column(name = "CODE")
    private Integer code;

    @Column(name = "DIVISION_TYPE", columnDefinition = "nvarchar(100)")
    private String divisionType;

    @Column(name = "CODE_NAME", length = 50)
    @FullTextField
    @ActiveReflection
    private String codeName;

    @Column(name = "PHONE_CODE")
    private Integer phoneCode;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "province")
    private List<District> districts = new ArrayList<>();

    @ActiveReflection
    protected Province() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of province must be integer");
        }
        this.id = (Integer) id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        } else {
            Deque<Province> results = new LinkedList<>();
            Province first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (Province) entity;
                } else {
                    Province casted = (Province) entity;
                    if (casted.getDistricts().isEmpty()) {
                        results.add(casted);
                    } else {
                        first.getDistricts().addAll(casted.getDistricts());
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
    public void setCode(Integer code) {
        if (this.code != null && !this.code.equals(code)) {
            trackUpdatedField("CODE", this.code, code);
        }
        this.code = code;
    }

    @ActiveReflection
    public void setDivisionType(String divisionType) {
        if (this.divisionType != null && !this.divisionType.equals(divisionType)) {
            trackUpdatedField("DIVISION_TYPE", this.divisionType, divisionType);
        }
        this.divisionType = divisionType;
    }

    @ActiveReflection
    public void setCodeName(String codeName) {
        if (this.codeName != null && !this.codeName.equals(codeName)) {
            trackUpdatedField("CODE_NAME", this.codeName, codeName);
        }
        this.codeName = codeName;
    }

    @ActiveReflection
    public void setPhoneCode(Integer phoneCode) {
        if (this.phoneCode != null && !this.phoneCode.equals(phoneCode)) {
            trackUpdatedField("PHONE_CODE", this.phoneCode, phoneCode);
        }
        this.phoneCode = phoneCode;
    }

    @ActiveReflection
    public void setDistricts(List<District> districts) {
        this.districts = districts;
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

    public String getDivisionType() {
        return this.divisionType;
    }

    public String getCodeName() {
        return this.codeName;
    }

    public Integer getPhoneCode() {
        return this.phoneCode;
    }

    public List<District> getDistricts() {
        return this.districts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Province province)) return false;
        if (!super.equals(o)) return false;
        return Objects.equal(getId(), province.getId()) && Objects.equal(getName(), province.getName()) && Objects.equal(getCode(), province.getCode()) && Objects.equal(getDivisionType(), province.getDivisionType()) && Objects.equal(getCodeName(), province.getCodeName()) && Objects.equal(getPhoneCode(), province.getPhoneCode()) && Objects.equal(getDistricts(), province.getDistricts());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getId(), getName(), getCode(), getDivisionType(), getCodeName(), getPhoneCode(), getDistricts());
    }
}
