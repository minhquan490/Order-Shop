package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class OrderOfCustomerResp {

    @JsonProperty("infos")
    private Collection<OrderInfo> orderInfos;

    @JsonProperty("total")
    private Long totalOrder;

    @JsonProperty("page")
    private Long page;

    @JsonProperty("page_size")
    private Long pageSize;

    public OrderOfCustomerResp() {
    }

    public Collection<OrderInfo> getOrderInfos() {
        return this.orderInfos;
    }

    public Long getTotalOrder() {
        return this.totalOrder;
    }

    public Long getPage() {
        return this.page;
    }

    public Long getPageSize() {
        return this.pageSize;
    }

    @JsonProperty("infos")
    public void setOrderInfos(Collection<OrderInfo> orderInfos) {
        this.orderInfos = orderInfos;
    }

    @JsonProperty("total")
    public void setTotalOrder(Long totalOrder) {
        this.totalOrder = totalOrder;
    }

    @JsonProperty("page")
    public void setPage(Long page) {
        this.page = page;
    }

    @JsonProperty("page_size")
    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public static class OrderInfo {

        @JsonProperty("id")
        private String id;

        @JsonProperty("order_time")
        private String orderTime;

        @JsonProperty("order_status")
        private String status;

        public OrderInfo() {
        }

        public String getId() {
            return this.id;
        }

        public String getOrderTime() {
            return this.orderTime;
        }

        public String getStatus() {
            return this.status;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("order_time")
        public void setOrderTime(String orderTime) {
            this.orderTime = orderTime;
        }

        @JsonProperty("order_status")
        public void setStatus(String status) {
            this.status = status;
        }
    }
}
