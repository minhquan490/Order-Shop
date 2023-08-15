package com.bachlinh.order.repository.query;

import jakarta.persistence.criteria.JoinType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class Join {
    private final String attribute;
    private final JoinType type;
}
