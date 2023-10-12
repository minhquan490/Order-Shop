package com.bachlinh.order.entity.repository.query;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.utils.QueryUtils;

class SqlDeleteSqm implements SqlDelete {
    private static final String ID_PROPERTY = QueryUtils.getIdProperty();
    private static final String DELETE_TEMPLATE = "DELETE FROM {0} WHERE ID = :" + ID_PROPERTY;

    private final TableMetadataHolder table;
    private final List<QueryBinding> queryBindings = new LinkedList<>();

    SqlDeleteSqm(TableMetadataHolder table) {
        this.table = table;
    }

    @Override
    public String getNativeQuery() {
        return MessageFormat.format(DELETE_TEMPLATE, table.getTableName());
    }

    @Override
    public Collection<QueryBinding> getQueryBindings() {
        return queryBindings;
    }

    @Override
    public SqlDelete delete(AbstractEntity<?> table) {
        Object id = table.getId();
        QueryBinding queryBinding = new QueryBinding(ID_PROPERTY, id);
        queryBindings.add(queryBinding);
        return this;
    }
}
