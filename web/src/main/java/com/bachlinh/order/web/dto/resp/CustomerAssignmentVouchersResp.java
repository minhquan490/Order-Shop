package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@NoArgsConstructor
@Getter
@Setter
public class CustomerAssignmentVouchersResp {

    @JsonProperty("vouchers")
    private Collection<AssignmentVoucher> vouchers;

    @JsonProperty("total_vouchers")
    private Long totalVouchers;

    @JsonProperty("page")
    private Long page;

    @JsonProperty("page_size")
    private Long pageSize;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class AssignmentVoucher {

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("discount_percent")
        private String discountPercent;

        @JsonProperty("time_start")
        private String timeStart;

        @JsonProperty("time_expired")
        private String timeExpired;
    }
}
