package com.bachlinh.order.entity.repository.query;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.BaseEntity;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class AbstractSqlUpdate implements SqlUpdate {
    private final TableMetadataHolder metadataHolder;
    private final List<UpdatedFieldHolder> updatedFieldHolders = new LinkedList<>();
    private final Set<QueryBinding> bindingSet = new LinkedHashSet<>();

    private BaseEntity<?> target;

    protected AbstractSqlUpdate(TableMetadataHolder metadataHolder) {
        this.metadataHolder = metadataHolder;
    }

    @Override
    public String getNativeQuery() {
        String template = getUpdatePattern();
        String setComponent = resolveUpdateValue();
        bindingSet.addAll(updatedFieldHolders.stream()
                .map(updatedFieldHolder -> new QueryBinding(updatedFieldHolder.bindingAttribute(), updatedFieldHolder.value()))
                .toList());
        bindingSet.add(new QueryBinding("entityId", target.getId()));
        return MessageFormat.format(template, metadataHolder.getTableName(), setComponent, ":entityId");
    }

    @Override
    public SqlUpdate update(AbstractEntity<?> entity) {
        this.target = entity;
        int index = -1;
        var updatedFields = entity.getUpdatedFields();
        int stopCondition = updatedFields.size() - 1;
        do {
            index++;
            var field = updatedFields.get(index);
            UpdatedFieldHolder holder = new UpdatedFieldHolder(field.fieldName(), field.newValue().get(), "f" + index);
            updatedFieldHolders.add(holder);
        } while (index < stopCondition);
        return this;
    }

    @Override
    public Collection<QueryBinding> getQueryBindings() {
        return bindingSet;
    }

    protected abstract String getUpdatePattern();

    private String resolveUpdateValue() {
        String template = "{0} = {1}";
        return String.join(", ",
                updatedFieldHolders.stream()
                        .map(updatedFieldHolder -> MessageFormat.format(template, updatedFieldHolder.columnName(), ":".concat(updatedFieldHolder.bindingAttribute())))
                        .toList()
                        .toArray(new String[0]));
    }

    private record UpdatedFieldHolder(String columnName, Object value, String bindingAttribute) {
    }
}
