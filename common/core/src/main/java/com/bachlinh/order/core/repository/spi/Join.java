package com.bachlinh.order.core.repository.spi;

import jakarta.persistence.criteria.JoinType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Join {
    private final String attribute;
    private final JoinType type;
}
