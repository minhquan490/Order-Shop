package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.Arrays;

public record ConfirmEmailResp(@JsonProperty("status") int status, @JsonProperty("messages") String[] messages) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmEmailResp that = (ConfirmEmailResp) o;
        return status == that.status && Objects.equal(messages, that.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(status, messages);
    }

    @Override
    public String toString() {
        return "ConfirmEmailResp{" +
                "status=" + status +
                ", messages=" + Arrays.toString(messages) +
                '}';
    }
}
