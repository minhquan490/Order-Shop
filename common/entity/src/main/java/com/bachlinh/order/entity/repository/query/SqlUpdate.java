package com.bachlinh.order.entity.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface SqlUpdate extends NativeQueryHolder {
    SqlUpdate update(AbstractEntity<?> entity);
}
