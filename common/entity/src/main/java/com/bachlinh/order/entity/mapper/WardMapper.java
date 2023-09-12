package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.annotation.ResultMapper;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.Ward;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class WardMapper extends AbstractEntityMapper<Ward> {
    @Override
    protected String getTableName() {
        return Ward.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        Ward result = getEntityFactory().getEntity(Ward.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("WARD")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(District.class);
            if (mapper.canMap(resultSet)) {
                var district = mapper.map(resultSet);
                if (district != null) {
                    district.getWards().add(result);
                    result.setDistrict(district);
                }
            }
        }
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
    }

    @Override
    protected void setData(Ward target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "WARD.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "WARD.NAME" -> {
                target.setName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "WARD.CODE" -> {
                target.setCode((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "WARD.CODE_NAME" -> {
                target.setCodeName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "WARD.DIVISION_TYPE" -> {
                target.setDivisionType((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "WARD.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "WARD.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "WARD.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "WARD.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
