package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.entity.model.Ward;

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
public class DistrictMapper extends AbstractEntityMapper<District> {

    @Override
    protected void assignMultiTable(District target, Collection<BaseEntity<?>> results) {

        for (BaseEntity<?> result : results) {
            Ward ward = (Ward) result;
            ward.setDistrict(target);
        }

        Set<Ward> wardSet = new LinkedHashSet<>(results.stream().map(Ward.class::cast).toList());
        target.setWards(new LinkedList<>(wardSet));
    }

    @Override
    protected void assignSingleTable(District target, BaseEntity<?> mapped) {
        Province province = (Province) mapped;
        province.getDistricts().add(target);
        target.setProvince(province);
    }

    @Override
    protected String getTableName() {
        return District.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        District result = getEntityFactory().getEntity(District.class);
        result.setWards(Collections.emptyList());
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Province.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Ward.class);
            List<BaseEntity<?>> wardSet = new LinkedList<>();

            mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, wardSet, "WARD");
        }

        return wrap(result, wrapped.get());
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
