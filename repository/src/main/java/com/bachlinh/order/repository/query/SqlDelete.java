package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface SqlDelete extends NativeQueryHolder {

    SqlDelete delete(AbstractEntity<?> table);
}
