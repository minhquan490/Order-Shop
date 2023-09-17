package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class OrderOfCustomerResp {

    @JsonProperty("infos")
    private Collection<OrderInfo> orderInfos;

    @JsonProperty("total")
    private Long totalOrder;

    @JsonProperty("page")
    private Long page;

    @JsonProperty("page_size")
    private Long pageSize;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class OrderInfo {

        @JsonProperty("id")
        private String id;

        @JsonProperty("order_time")
        private String orderTime;

        @JsonProperty("order_status")
        private String status;
    }
}
