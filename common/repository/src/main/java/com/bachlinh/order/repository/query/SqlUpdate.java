package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface SqlUpdate extends NativeQueryHolder {
    SqlUpdate update(AbstractEntity<?> entity);
}
