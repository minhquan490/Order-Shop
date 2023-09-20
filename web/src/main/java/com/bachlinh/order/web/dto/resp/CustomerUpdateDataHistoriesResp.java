package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class CustomerUpdateDataHistoriesResp {

    @JsonProperty("histories")
    Collection<CustomerUpdateDataHistoryInfo> historyInfos;

    @JsonProperty("total_histories")
    private Long totalHistories;

    @JsonProperty("page")
    private Long page;

    @JsonProperty("page_size")
    private Long pageSize;

    public CustomerUpdateDataHistoriesResp() {
    }

    public Collection<CustomerUpdateDataHistoryInfo> getHistoryInfos() {
        return this.historyInfos;
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

    @JsonProperty("histories")
    public void setHistoryInfos(Collection<CustomerUpdateDataHistoryInfo> historyInfos) {
        this.historyInfos = historyInfos;
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

    public static class CustomerUpdateDataHistoryInfo {

        @JsonProperty("id")
        private String id;

        @JsonProperty("old_value")
        private String oldValue;

        @JsonProperty("field_name")
        private String fieldName;

        @JsonProperty("time_update")
        private String timeUpdate;

        public CustomerUpdateDataHistoryInfo() {
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
