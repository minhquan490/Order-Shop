package com.bachlinh.order.web.dto.form;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Objects;

public record OrderChangeStatusForm(@JsonAlias("id") String orderId, @JsonAlias("status") String status) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderChangeStatusForm that)) return false;
        return Objects.equals(orderId, that.orderId) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, status);
    }
}
