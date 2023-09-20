package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class CustomerAccessHistoriesResp {

    @JsonProperty("access_histories")
    private Collection<CustomerAccessHistoriesInfo> accessHistories;

    @JsonProperty("total_histories")
    private Long totalHistories;

    @JsonProperty("page")
    private Long page;

    @JsonProperty("page_size")
    private Long pageSize;

    public CustomerAccessHistoriesResp() {
    }

    public Collection<CustomerAccessHistoriesInfo> getAccessHistories() {
        return this.accessHistories;
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

    @JsonProperty("access_histories")
    public void setAccessHistories(Collection<CustomerAccessHistoriesInfo> accessHistories) {
        this.accessHistories = accessHistories;
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

        public CustomerAccessHistoriesInfo() {
        }

        public String getId() {
            return this.id;
        }

        public String getPathRequest() {
            return this.pathRequest;
        }

        public String getRequestType() {
            return this.requestType;
        }

        public String getRequestContent() {
            return this.requestContent;
        }

        public String getCustomerIp() {
            return this.customerIp;
        }

        public String getRemoveTime() {
            return this.removeTime;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("path_request")
        public void setPathRequest(String pathRequest) {
            this.pathRequest = pathRequest;
        }

        @JsonProperty("request_type")
        public void setRequestType(String requestType) {
            this.requestType = requestType;
        }

        @JsonProperty("request_content")
        public void setRequestContent(String requestContent) {
            this.requestContent = requestContent;
        }

        @JsonProperty("customer_ip")
        public void setCustomerIp(String customerIp) {
            this.customerIp = customerIp;
        }

        @JsonProperty("remove_time")
        public void setRemoveTime(String removeTime) {
            this.removeTime = removeTime;
        }
    }
}
