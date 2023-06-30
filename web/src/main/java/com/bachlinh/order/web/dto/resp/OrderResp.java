package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

public record OrderResp(@JsonProperty("id") String id,
                        @JsonProperty("time_order") String timeOrder,
                        @JsonProperty("status") String status,
                        @JsonProperty("details") OrderDetailResp[] details) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderResp orderResp)) return false;
        return Objects.equals(id, orderResp.id) && Objects.equals(timeOrder, orderResp.timeOrder) && Objects.equals(status, orderResp.status) && Arrays.equals(details, orderResp.details);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, timeOrder, status);
        result = 31 * result + Arrays.hashCode(details);
        return result;
    }

    @Override
    public String toString() {
        return "OrderResp{" +
                "id='" + id + '\'' +
                ", timeOrder='" + timeOrder + '\'' +
                ", status='" + status + '\'' +
                ", details=" + Arrays.toString(details) +
                '}';
    }

    public record OrderDetailResp(@JsonProperty("amount") String amount,
                                  @JsonProperty("product_id") String productId,
                                  @JsonProperty("product_name") String productName) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof OrderDetailResp that)) return false;
            return Objects.equals(amount, that.amount) && Objects.equals(productId, that.productId) && Objects.equals(productName, that.productName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(amount, productId, productName);
        }
    }
}
