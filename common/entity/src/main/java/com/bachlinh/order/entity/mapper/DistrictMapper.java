package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.entity.model.Ward;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class DistrictMapper extends AbstractEntityMapper<District> {
    @Override
    protected String getTableName() {
        return District.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        District result = getEntityFactory().getEntity(District.class);
        result.setWards(Collections.emptyList());
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("DISTRICT")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Province.class);
            if (mapper.canMap(resultSet)) {
                var province = mapper.map(resultSet);
                if (province != null) {
                    province.getDistricts().add(result);
                    result.setProvince(province);
                }
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Ward.class);
            List<Ward> wardSet = new LinkedList<>();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("WARD")) {
                    var ward = mapper.map(resultSet);
                    if (ward != null) {
                        ward.setDistrict(result);
                        wardSet.add(ward);
                    }
                } else {
                    break;
                }
            }
            result.setWards(wardSet);
        }
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
    }

    @Override
    protected void setData(District target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "DISTRICT.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DISTRICT.NAME" -> {
                target.setName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DISTRICT.CODE" -> {
                target.setCode((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DISTRICT.DIVISION_TYPE" -> {
                target.setDivisionType((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DISTRICT.CODE_NAME" -> {
                target.setCodeName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DISTRICT.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DISTRICT.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DISTRICT.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DISTRICT.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
