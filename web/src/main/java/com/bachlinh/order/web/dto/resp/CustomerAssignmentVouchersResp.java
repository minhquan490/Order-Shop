package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class CustomerAssignmentVouchersResp {

    @JsonProperty("vouchers")
    private Collection<AssignmentVoucher> vouchers;

    @JsonProperty("total_vouchers")
    private Long totalVouchers;

    @JsonProperty("page")
    private Long page;

    @JsonProperty("page_size")
    private Long pageSize;

    public CustomerAssignmentVouchersResp() {
    }

    public Collection<AssignmentVoucher> getVouchers() {
        return this.vouchers;
    }

    public Long getTotalVouchers() {
        return this.totalVouchers;
    }

    public Long getPage() {
        return this.page;
    }

    public Long getPageSize() {
        return this.pageSize;
    }

    @JsonProperty("vouchers")
    public void setVouchers(Collection<AssignmentVoucher> vouchers) {
        this.vouchers = vouchers;
    }

    @JsonProperty("total_vouchers")
    public void setTotalVouchers(Long totalVouchers) {
        this.totalVouchers = totalVouchers;
    }

    @JsonProperty("page")
    public void setPage(Long page) {
        this.page = page;
    }

    @JsonProperty("page_size")
    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

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

        public AssignmentVoucher() {
        }

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getDiscountPercent() {
            return this.discountPercent;
        }

        public String getTimeStart() {
            return this.timeStart;
        }

        public String getTimeExpired() {
            return this.timeExpired;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("discount_percent")
        public void setDiscountPercent(String discountPercent) {
            this.discountPercent = discountPercent;
        }

        @JsonProperty("time_start")
        public void setTimeStart(String timeStart) {
            this.timeStart = timeStart;
        }

        @JsonProperty("time_expired")
        public void setTimeExpired(String timeExpired) {
            this.timeExpired = timeExpired;
        }
    }
}
