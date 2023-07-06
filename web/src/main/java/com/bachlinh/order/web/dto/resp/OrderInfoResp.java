package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OrderInfoResp {

    @JsonProperty("id")
    private String id;

    @JsonProperty("is_deposited")
    private boolean deposited;

    @JsonProperty("time_order")
    private String timeOrder;

    @JsonProperty("status")
    private String orderStatus;

    @JsonProperty("details")
    private Details[] details;

    @JsonProperty("total_price")
    private int totalPrice;

    @JsonProperty("transaction_code")
    private String transactionCode;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Details {

        @JsonProperty("amount")
        private int amount;

        @JsonProperty("product_name")
        private String productName;

        @JsonProperty("product_price")
        private int productPrice;
    }
}
