package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
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

    @Getter
    @Setter
    @NoArgsConstructor
    public static class History {

        @JsonProperty("path_request")
        private String pathRequest;

        @JsonProperty("request_type")
        private String requestType;

        @JsonProperty("request_time")
        private String requestTime;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Voucher {

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Order {

        @JsonProperty("id")
        private String id;

        @JsonProperty("time_order")
        private String timeOrder;

        @JsonProperty("transaction_code")
        private String transactionCode;

        @JsonProperty("status")
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginHistory {

        @JsonProperty("id")
        private String id;

        @JsonProperty("last_login_time")
        private String lastLoginTime;

        @JsonProperty("login_ip")
        private String loginIp;

        @JsonProperty("is_success")
        private boolean isSuccess;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class InfoChangeHistory {

        @JsonProperty("id")
        private String id;

        @JsonProperty("old_value")
        private String oldValue;

        @JsonProperty("field_name")
        private String fieldName;

        @JsonProperty("time_update")
        private String timeUpdate;
    }
}
