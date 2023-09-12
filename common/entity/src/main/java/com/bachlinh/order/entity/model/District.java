package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.QueryCache;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
        name = "DISTRICT",
        indexes = {
                @Index(name = "idx_district_name", columnList = "NAME"),
                @Index(name = "idx_district_code", columnList = "CODE")
        }
)
@EnableFullTextSearch
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = true)
@QueryCache
public class District extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "NAME", columnDefinition = "nvarchar(100)")
    @FullTextField
    @ActiveReflection
    private String name;

    @Column(name = "CODE")
    private Integer code;

    @Column(name = "DIVISION_TYPE", columnDefinition = "nvarchar(100)")
    private String divisionType;

    @Column(name = "CODE_NAME", length = 100)
    @FullTextField
    @ActiveReflection
    private String codeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROVINCE_ID", nullable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Province province;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "district")
    @EqualsAndHashCode.Exclude
    private List<Ward> wards = new ArrayList<>();

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of district must be int");
        }
        this.id = (Integer) id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        } else {
            Deque<District> results = new LinkedList<>();
            District first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (District) entity;
                } else {
                    District casted = (District) entity;
                    if (casted.getWards().isEmpty()) {
                        results.addFirst(casted);
                    } else {
                        first.getWards().addAll(casted.getWards());
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
    public void setProvince(Province province) {
        if (this.province != null && !this.province.getId().equals(Objects.requireNonNull(province).getId())) {
            trackUpdatedField("PROVINCE_ID", this.province.getId(), province.getId());
        }
        this.province = province;
    }

    @ActiveReflection
    public void setWards(List<Ward> wards) {
        this.wards = wards;
    }
}