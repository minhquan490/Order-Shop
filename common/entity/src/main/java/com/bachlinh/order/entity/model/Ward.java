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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;

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
    @Fetch(FetchMode.JOIN)
    private District district;

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
    public <U extends BaseEntity<Integer>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
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
        if (this.district != null && !Objects.requireNonNull(this.district.getId()).equals(district.getId())) {
            trackUpdatedField("DISTRICT_ID", Objects.requireNonNull(this.district.getId()).toString());
        }
        this.district = district;
    }

    public static EntityMapper<Ward> getMapper() {
        return new WardMapper();
    }

    private static class WardMapper implements EntityMapper<Ward> {

        @Override
        public Ward map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new Ward().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public Ward map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            Ward result = new Ward();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().startsWith("WARD")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = District.getMapper();
                var district = mapper.map(resultSet);
                district.getWards().add(result);
                result.setDistrict(district);
            }
            return result;
        }

        private void setData(Ward target, MappingObject mappingObject) {
            switch (mappingObject.columnName()) {
                case "WARD.ID" -> target.setId(mappingObject.value());
                case "WARD.NAME" -> target.setName((String) mappingObject.value());
                case "WARD.CODE" -> target.setCode((Integer) mappingObject.value());
                case "WARD.CODE_NAME" -> target.setCodeName((String) mappingObject.value());
                case "WARD.DIVISION_TYPE" -> target.setDivisionType((String) mappingObject.value());
                case "WARD.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "WARD.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "WARD.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "WARD.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
