package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = true)
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
    @EqualsAndHashCode.Exclude
    private List<District> districts = new ArrayList<>();

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
    public <U extends BaseEntity<Integer>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
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

    public static EntityMapper<Province> getMapper() {
        return new ProvinceMapper();
    }

    private static class ProvinceMapper implements EntityMapper<Province> {

        @Override
        public Province map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new Province().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public Province map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            Province result = new Province();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("PROVINCE")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = District.getMapper();
                List<District> districtSet = new LinkedList<>();
                while (!resultSet.isEmpty()) {
                    hook = resultSet.peek();
                    if (hook.columnName().split("\\.")[0].equals("DISTRICT")) {
                        var district = mapper.map(resultSet);
                        district.setProvince(result);
                        districtSet.add(district);
                    } else {
                        break;
                    }
                }
                result.setDistricts(districtSet);
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("PROVINCE");
            });
        }

        private void setData(Province target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "PROVINCE.ID" -> target.setId(mappingObject.value());
                case "PROVINCE.NAME" -> target.setName((String) mappingObject.value());
                case "PROVINCE.CODE" -> target.setCode((Integer) mappingObject.value());
                case "PROVINCE.DIVISION_TYPE" -> target.setDivisionType((String) mappingObject.value());
                case "PROVINCE.CODE_NAME" -> target.setCodeName((String) mappingObject.value());
                case "PROVINCE.PHONE_CODE" -> target.setPhoneCode((Integer) mappingObject.value());
                case "PROVINCE.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "PROVINCE.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "PROVINCE.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "PROVINCE.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
