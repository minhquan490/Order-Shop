package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class CustomerInfoResp {

    @JsonProperty("id")
    private String id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("role")
    private String role;

    @JsonProperty("username")
    private String username;

    @JsonProperty("order_point")
    private Integer orderPoint;

    @JsonProperty("address")
    private Collection<String> address;

    @JsonProperty("is_activated")
    private boolean activated;

    @JsonProperty("is_account_non_expired")
    private boolean accountNonExpired;

    @JsonProperty("is_account_non_locked")
    private boolean accountNonLocked;

    @JsonProperty("is_credentials_non_expired")
    private boolean credentialsNonExpired;

    @JsonProperty("is_enabled")
    private boolean enabled;

    @JsonProperty("picture")
    private String picture;

    @JsonProperty("histories")
    private History[] histories;

    @JsonProperty("vouchers")
    private Voucher[] vouchers;

    @JsonProperty("orders")
    private Order[] orders;

    @JsonProperty("login_histories")
    private LoginHistory[] loginHistories;

    @JsonProperty("change_histories")
    private InfoChangeHistory[] infoChangeHistories;

    public CustomerInfoResp() {
    }

    public String getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public String getGender() {
        return this.gender;
    }

    public String getRole() {
        return this.role;
    }

    public String getUsername() {
        return this.username;
    }

    public Integer getOrderPoint() {
        return this.orderPoint;
    }

    public Collection<String> getAddress() {
        return this.address;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getPicture() {
        return this.picture;
    }

    public History[] getHistories() {
        return this.histories;
    }

    public Voucher[] getVouchers() {
        return this.vouchers;
    }

    public Order[] getOrders() {
        return this.orders;
    }

    public LoginHistory[] getLoginHistories() {
        return this.loginHistories;
    }

    public InfoChangeHistory[] getInfoChangeHistories() {
        return this.infoChangeHistories;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("phone")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("gender")
    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonProperty("role")
    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("order_point")
    public void setOrderPoint(Integer orderPoint) {
        this.orderPoint = orderPoint;
    }

    @JsonProperty("address")
    public void setAddress(Collection<String> address) {
        this.address = address;
    }

    @JsonProperty("is_activated")
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @JsonProperty("is_account_non_expired")
    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @JsonProperty("is_account_non_locked")
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @JsonProperty("is_credentials_non_expired")
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @JsonProperty("is_enabled")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonProperty("picture")
    public void setPicture(String picture) {
        this.picture = picture;
    }

    @JsonProperty("histories")
    public void setHistories(History[] histories) {
        this.histories = histories;
    }

    @JsonProperty("vouchers")
    public void setVouchers(Voucher[] vouchers) {
        this.vouchers = vouchers;
    }

    @JsonProperty("orders")
    public void setOrders(Order[] orders) {
        this.orders = orders;
    }

    @JsonProperty("login_histories")
    public void setLoginHistories(LoginHistory[] loginHistories) {
        this.loginHistories = loginHistories;
    }

    @JsonProperty("change_histories")
    public void setInfoChangeHistories(InfoChangeHistory[] infoChangeHistories) {
        this.infoChangeHistories = infoChangeHistories;
    }

    public static class History {

        @JsonProperty("path_request")
        private String pathRequest;

        @JsonProperty("request_type")
        private String requestType;

        @JsonProperty("request_time")
        private String requestTime;

        public History() {
        }

        public String getPathRequest() {
            return this.pathRequest;
        }

        public String getRequestType() {
            return this.requestType;
        }

        public String getRequestTime() {
            return this.requestTime;
        }

        @JsonProperty("path_request")
        public void setPathRequest(String pathRequest) {
            this.pathRequest = pathRequest;
        }

        @JsonProperty("request_type")
        public void setRequestType(String requestType) {
            this.requestType = requestType;
        }

        @JsonProperty("request_time")
        public void setRequestTime(String requestTime) {
            this.requestTime = requestTime;
        }
    }

    public static class Voucher {

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        public Voucher() {
        }

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Order {

        @JsonProperty("id")
        private String id;

        @JsonProperty("time_order")
        private String timeOrder;

        @JsonProperty("transaction_code")
        private String transactionCode;

        @JsonProperty("status")
        private String status;

        public Order() {
        }

        public String getId() {
            return this.id;
        }

        public String getTimeOrder() {
            return this.timeOrder;
        }

        public String getTransactionCode() {
            return this.transactionCode;
        }

        public String getStatus() {
            return this.status;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("time_order")
        public void setTimeOrder(String timeOrder) {
            this.timeOrder = timeOrder;
        }

        @JsonProperty("transaction_code")
        public void setTransactionCode(String transactionCode) {
            this.transactionCode = transactionCode;
        }

        @JsonProperty("status")
        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class LoginHistory {

        @JsonProperty("id")
        private String id;

        @JsonProperty("last_login_time")
        private String lastLoginTime;

        @JsonProperty("login_ip")
        private String loginIp;

        @JsonProperty("is_success")
        private boolean isSuccess;

        public LoginHistory() {
        }

        public String getId() {
            return this.id;
        }

        public String getLastLoginTime() {
            return this.lastLoginTime;
        }

        public String getLoginIp() {
            return this.loginIp;
        }

        public boolean isSuccess() {
            return this.isSuccess;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("last_login_time")
        public void setLastLoginTime(String lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
        }

        @JsonProperty("login_ip")
        public void setLoginIp(String loginIp) {
            this.loginIp = loginIp;
        }

        @JsonProperty("is_success")
        public void setSuccess(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }
    }

    public static class InfoChangeHistory {

        @JsonProperty("id")
        private String id;

        @JsonProperty("old_value")
        private String oldValue;

        @JsonProperty("field_name")
        private String fieldName;

        @JsonProperty("time_update")
        private String timeUpdate;

        public InfoChangeHistory() {
        }

        public String getId() {
            return this.id;
        }

        public String getOldValue() {
            return this.oldValue;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public String getTimeUpdate() {
            return this.timeUpdate;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("old_value")
        public void setOldValue(String oldValue) {
            this.oldValue = oldValue;
        }

        @JsonProperty("field_name")
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        @JsonProperty("time_update")
        public void setTimeUpdate(String timeUpdate) {
            this.timeUpdate = timeUpdate;
        }
    }
}
