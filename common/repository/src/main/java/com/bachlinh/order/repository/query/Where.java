package com.bachlinh.order.repository.query;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Where {
    private final Operator operator;
    private final String attribute;
    private final Object value;
}
