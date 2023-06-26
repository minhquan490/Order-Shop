package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OrderListResp {

    @JsonProperty("id")
    private String id;

    @JsonProperty("is_deposited")
    private String deposited;

    @JsonProperty("time_order")
    private String timeOrder;

    @JsonProperty("status")
    private String orderStatus;

    @JsonProperty("customer_name")
    private String customerName;
}
