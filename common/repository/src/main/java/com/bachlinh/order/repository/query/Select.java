package com.bachlinh.order.repository.query;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class Select {
    private final String column;
}
