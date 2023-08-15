package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

import java.util.Collection;

public interface WhereOperation extends NativeQueryHolder, SqlOrderBy<WhereOperation>, SqlLimitOffset<WhereOperation> {

    Collection<QueryBinding> getQueryBindings();

    WhereOperation where(Where where);

    WhereOperation where(Where where, Class<? extends AbstractEntity<?>> table);
}
