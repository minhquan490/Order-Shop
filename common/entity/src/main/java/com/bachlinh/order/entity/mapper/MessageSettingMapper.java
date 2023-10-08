package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.MessageSetting;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class MessageSettingMapper extends AbstractEntityMapper<MessageSetting> {

    @Override
    protected void assignMultiTable(MessageSetting target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(MessageSetting target, BaseEntity<?> mapped) {
        // Do nothing
    }

    @Override
    protected String getTableName() {
        return MessageSetting.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MessageSetting result = getEntityFactory().getEntity(MessageSetting.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
    }

    @Override
    protected void setData(MessageSetting target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "MESSAGE_SETTING.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "MESSAGE_SETTING.VALUE" -> {
                target.setValue((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "MESSAGE_SETTING.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "MESSAGE_SETTING.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "MESSAGE_SETTING.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "MESSAGE_SETTING.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
