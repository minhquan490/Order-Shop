package com.bachlinh.order.entity.repository.query;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class Where {
    private final Operation operation;
    private final String attribute;
    private final Object value;
}
