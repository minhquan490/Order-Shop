package com.bachlinh.order.entity.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface SqlDelete extends NativeQueryHolder {

    SqlDelete delete(AbstractEntity<?> table);
}
