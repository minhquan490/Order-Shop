package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public record OrderResp(@JsonProperty("id") String id, @JsonProperty("time_order") String timeOrder) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderResp orderResp)) return false;
        return Objects.equals(id, orderResp.id) && Objects.equals(timeOrder, orderResp.timeOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeOrder);
    }
}
