package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class EmailTemplateMapper extends AbstractEntityMapper<EmailTemplate> {

    @Override
    protected void assignMultiTable(EmailTemplate target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(EmailTemplate target, BaseEntity<?> mapped) {
        if (mapped instanceof Customer owner) {
            target.setOwner(owner);
        }
        if (mapped instanceof EmailTemplateFolder folder) {
            folder.getEmailTemplates().add(target);
            target.setFolder(folder);
        }
    }

    @Override
    protected String getTableName() {
        return EmailTemplate.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        EmailTemplate result = getEntityFactory().getEntity(EmailTemplate.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(EmailTemplateFolder.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }
        
        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(EmailTemplate target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "EMAIL_TEMPLATE.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.NAME" -> {
                target.setName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.TITLE" -> {
                target.setTitle((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.CONTENT" -> {
                target.setContent((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.EXPIRY_POLICY" -> {
                target.setExpiryPolicy((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.TOTAL_ARGUMENT" -> {
                target.setTotalArgument((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.PARAMS" -> {
                target.setParams((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
