package com.bachlinh.order.core.http;

import com.google.common.base.Objects;

public record NativeCookie(String path,
                           int maxAge,
                           boolean secure,
                           boolean httpOnly,
                           String value,
                           String name,
                           String domain) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NativeCookie that = (NativeCookie) o;
        return maxAge == that.maxAge && secure == that.secure && httpOnly == that.httpOnly && Objects.equal(path, that.path) && Objects.equal(value, that.value) && Objects.equal(name, that.name) && Objects.equal(domain, that.domain);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path, maxAge, secure, httpOnly, value, name, domain);
    }
}
