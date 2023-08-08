package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

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
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@EnableFullTextSearch
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
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
    private District district;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of ward must be int");
        }
        this.id = (Integer) id;
    }

    @ActiveReflection
    public void setName(String name) {
        if (this.name != null && !this.name.equals(name)) {
            trackUpdatedField("NAME", this.name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setCode(Integer code) {
        if (this.code != null && !this.code.equals(code)) {
            trackUpdatedField("CODE", this.code.toString());
        }
        this.code = code;
    }

    @ActiveReflection
    public void setCodeName(String codeName) {
        if (this.codeName != null && !this.codeName.equals(codeName)) {
            trackUpdatedField("CODE_NAME", this.codeName);
        }
        this.codeName = codeName;
    }

    @ActiveReflection
    public void setDivisionType(String divisionType) {
        if (this.divisionType != null && !this.divisionType.equals(divisionType)) {
            trackUpdatedField("DIVISION_TYPE", this.divisionType);
        }
        this.divisionType = divisionType;
    }

    @ActiveReflection
    public void setDistrict(District district) {
        if (this.district != null && !this.district.getId().equals(district.getId())) {
            trackUpdatedField("DISTRICT_ID", this.district.getId().toString());
        }
        this.district = district;
    }
}
