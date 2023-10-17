package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.Province;

import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class ProvinceMapper extends AbstractEntityMapper<Province> {

    @Override
    protected void assignMultiTable(Province target, Collection<BaseEntity<?>> results) {
        Set<District> districtSet = new LinkedHashSet<>();

        for (BaseEntity<?> result : results) {
            District district = (District) result;
            district.setProvince(target);
            districtSet.add(district);
        }

        target.setDistricts(new LinkedList<>(districtSet));
    }

    @Override
    protected void assignSingleTable(Province target, BaseEntity<?> mapped) {
        // Do nothing
    }

    @Override
    protected String getTableName() {
        return Province.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        Province result = getEntityFactory().getEntity(Province.class);
        result.setDistricts(Collections.emptyList());
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(District.class);
            List<BaseEntity<?>> districtSet = new LinkedList<>();

            mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, districtSet, "DISTRICT");
        }

        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(Province target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "PROVINCE.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PROVINCE.NAME" -> {
                target.setName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PROVINCE.CODE" -> {
                target.setCode((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PROVINCE.DIVISION_TYPE" -> {
                target.setDivisionType((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PROVINCE.CODE_NAME" -> {
                target.setCodeName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PROVINCE.PHONE_CODE" -> {
                target.setPhoneCode((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PROVINCE.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PROVINCE.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PROVINCE.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PROVINCE.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
