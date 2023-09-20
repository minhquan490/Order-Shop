package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public OrderInfoResp() {
    }

    public String getId() {
        return this.id;
    }

    public boolean isDeposited() {
        return this.deposited;
    }

    public String getTimeOrder() {
        return this.timeOrder;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }

    public Details[] getDetails() {
        return this.details;
    }

    public int getTotalPrice() {
        return this.totalPrice;
    }

    public String getTransactionCode() {
        return this.transactionCode;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("is_deposited")
    public void setDeposited(boolean deposited) {
        this.deposited = deposited;
    }

    @JsonProperty("time_order")
    public void setTimeOrder(String timeOrder) {
        this.timeOrder = timeOrder;
    }

    @JsonProperty("status")
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @JsonProperty("details")
    public void setDetails(Details[] details) {
        this.details = details;
    }

    @JsonProperty("total_price")
    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    @JsonProperty("transaction_code")
    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public static class Details {

        @JsonProperty("amount")
        private int amount;

        @JsonProperty("product_name")
        private String productName;

        @JsonProperty("product_price")
        private int productPrice;

        public Details() {
        }

        public int getAmount() {
            return this.amount;
        }

        public String getProductName() {
            return this.productName;
        }

        public int getProductPrice() {
            return this.productPrice;
        }

        @JsonProperty("amount")
        public void setAmount(int amount) {
            this.amount = amount;
        }

        @JsonProperty("product_name")
        public void setProductName(String productName) {
            this.productName = productName;
        }

        @JsonProperty("product_price")
        public void setProductPrice(int productPrice) {
            this.productPrice = productPrice;
        }
    }
}
