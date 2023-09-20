package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class CustomerLoginHistoryResp {

    @JsonProperty("login_histories")
    private Collection<LoginHistoryInfo> loginHistories;

    @JsonProperty("total_histories")
    private Long totalHistories;

    @JsonProperty("page")
    private Long page;

    @JsonProperty("page_size")
    private Long pageSize;

    public CustomerLoginHistoryResp() {
    }

    public Collection<LoginHistoryInfo> getLoginHistories() {
        return this.loginHistories;
    }

    public Long getTotalHistories() {
        return this.totalHistories;
    }

    public Long getPage() {
        return this.page;
    }

    public Long getPageSize() {
        return this.pageSize;
    }

    @JsonProperty("login_histories")
    public void setLoginHistories(Collection<LoginHistoryInfo> loginHistories) {
        this.loginHistories = loginHistories;
    }

    @JsonProperty("total_histories")
    public void setTotalHistories(Long totalHistories) {
        this.totalHistories = totalHistories;
    }

    @JsonProperty("page")
    public void setPage(Long page) {
        this.page = page;
    }

    @JsonProperty("page_size")
    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public static class LoginHistoryInfo {
        @JsonProperty("id")
        private String id;

        @JsonProperty("last_login_time")
        private String lastLoginTime;

        @JsonProperty("login_ip")
        private String loginIp;

        @JsonProperty("success")
        private String success;

        public LoginHistoryInfo() {
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

        public String getSuccess() {
            return this.success;
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

        @JsonProperty("success")
        public void setSuccess(String success) {
            this.success = success;
        }
    }
}
