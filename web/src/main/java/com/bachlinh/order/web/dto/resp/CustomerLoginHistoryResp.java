package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@NoArgsConstructor
@Getter
@Setter
public class CustomerLoginHistoryResp {

    @JsonProperty("login_histories")
    private Collection<LoginHistoryInfo> loginHistories;

    @JsonProperty("total_histories")
    private Long totalHistories;

    @JsonProperty("page")
    private Long page;

    @JsonProperty("page_size")
    private Long pageSize;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginHistoryInfo {
        @JsonProperty("id")
        private String id;

        @JsonProperty("last_login_time")
        private String lastLoginTime;

        @JsonProperty("login_ip")
        private String loginIp;
        
        @JsonProperty("success")
        private String success;
    }
}
