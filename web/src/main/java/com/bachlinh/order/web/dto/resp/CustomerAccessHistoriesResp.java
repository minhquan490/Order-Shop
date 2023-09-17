package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@NoArgsConstructor
@Getter
@Setter
public class CustomerAccessHistoriesResp {

    @JsonProperty("access_histories")
    private Collection<CustomerAccessHistoriesInfo> accessHistories;

    @JsonProperty("total_histories")
    private Long totalHistories;

    @JsonProperty("page")
    private Long page;

    @JsonProperty("page_size")
    private Long pageSize;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CustomerAccessHistoriesInfo {

        @JsonProperty("id")
        private String id;

        @JsonProperty("path_request")
        private String pathRequest;

        @JsonProperty("request_type")
        private String requestType;

        @JsonProperty("request_content")
        private String requestContent;

        @JsonProperty("customer_ip")
        private String customerIp;

        @JsonProperty("remove_time")
        private String removeTime;
    }
}
