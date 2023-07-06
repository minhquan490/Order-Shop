package com.bachlinh.order.repository.query;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class OrderBy {
    private final String column;
    private final Type type;

    public enum Type {
        ASC,
        DESC
    }
}
