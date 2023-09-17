package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class CustomerUpdateDataHistoriesResp {

    @JsonProperty("histories")
    Collection<CustomerUpdateDataHistoryInfo> historyInfos;

    @JsonProperty("total_histories")
    private Long totalHistories;

    @JsonProperty("page")
    private Long page;

    @JsonProperty("page_size")
    private Long pageSize;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CustomerUpdateDataHistoryInfo {

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
