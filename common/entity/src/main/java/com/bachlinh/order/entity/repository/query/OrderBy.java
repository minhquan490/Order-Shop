package com.bachlinh.order.entity.repository.query;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public final class OrderBy {
    private final String column;
    private final Type type;

    public enum Type {
        ASC,
        DESC
    }
}
