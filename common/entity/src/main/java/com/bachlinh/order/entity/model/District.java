package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.Cacheable;
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
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

@Entity
@Table(
        name = "DISTRICT",
        indexes = {
                @Index(name = "idx_district_name", columnList = "NAME"),
                @Index(name = "idx_district_code", columnList = "CODE")
        }
)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "district")
@EnableFullTextSearch
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
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
    public <U extends BaseEntity<Integer>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
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
    public void setDivisionType(String divisionType) {
        if (this.divisionType != null && !this.divisionType.equals(divisionType)) {
            trackUpdatedField("DIVISION_TYPE", this.divisionType);
        }
        this.divisionType = divisionType;
    }

    @ActiveReflection
    public void setCodeName(String codeName) {
        if (this.codeName != null && !this.codeName.equals(codeName)) {
            trackUpdatedField("CODE_NAME", this.codeName);
        }
        this.codeName = codeName;
    }

    @ActiveReflection
    public void setProvince(@NonNull Province province) {
        if (this.province != null && !Objects.requireNonNull(this.province.getId()).equals(province.getId())) {
            trackUpdatedField("PROVINCE_ID", Objects.requireNonNull(this.province.getId()).toString());
        }
        this.province = province;
    }

    @ActiveReflection
    public void setWards(List<Ward> wards) {
        this.wards = wards;
    }

    public static EntityMapper<District> getMapper() {
        return new DistrictMapper();
    }

    private static class DistrictMapper implements EntityMapper<District> {

        @Override
        public District map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new District().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public District map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            District result = new District();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("DISTRICT")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Province.getMapper();
                if (mapper.canMap(resultSet)) {
                    var province = mapper.map(resultSet);
                    province.getDistricts().add(result);
                    result.setProvince(province);
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Ward.getMapper();
                List<Ward> wardSet = new LinkedList<>();
                while (!resultSet.isEmpty()) {
                    hook = resultSet.peek();
                    if (hook.columnName().split("\\.")[0].equals("WARD")) {
                        var ward = mapper.map(resultSet);
                        ward.setDistrict(result);
                        wardSet.add(ward);
                    } else {
                        break;
                    }
                }
                result.setWards(wardSet);
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("DISTRICT");
            });
        }

        private void setData(District target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "DISTRICT.ID" -> target.setId(mappingObject.value());
                case "DISTRICT.NAME" -> target.setName((String) mappingObject.value());
                case "DISTRICT.CODE" -> target.setCode((Integer) mappingObject.value());
                case "DISTRICT.DIVISION_TYPE" -> target.setDivisionType((String) mappingObject.value());
                case "DISTRICT.CODE_NAME" -> target.setCodeName((String) mappingObject.value());
                case "DISTRICT.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "DISTRICT.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "DISTRICT.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "DISTRICT.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}