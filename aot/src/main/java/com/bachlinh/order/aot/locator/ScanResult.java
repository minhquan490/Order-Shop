package com.bachlinh.order.aot.locator;

import org.springframework.lang.Nullable;

import java.util.Objects;

public record ScanResult<T>(@Nullable T result) {

    public boolean isReady() {
        return result != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScanResult<?> that = (ScanResult<?>) o;
        return Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result);
    }
}

