package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.Ward;

import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class WardMapper extends AbstractEntityMapper<Ward> {

    @Override
    protected void assignMultiTable(Ward target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(Ward target, BaseEntity<?> mapped) {
        District district = (District) mapped;
        district.getWards().add(target);
        target.setDistrict(district);
    }

    @Override
    protected String getTableName() {
        return Ward.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        Ward result = getEntityFactory().getEntity(Ward.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(District.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        return wrap(result, wrapped.get());
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
