package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.bachlinh.order.entity.model.CrawlResult;

public class CrawlResultResp {

    @JsonIgnore
    private final CrawlResult delegate;

    public CrawlResultResp(CrawlResult delegate) {
        this.delegate = delegate;
    }

    @JsonProperty("path")
    public String getSourcePath() {
        return delegate.getSourcePath();
    }

    @JsonProperty("resources")
    public String[] getResource() {
        String resource = delegate.getResources();
        if (resource.contains(",")) {
            return resource.split(",");
        } else {
            return new String[]{resource};
        }
    }

    @JsonProperty("time_finish")
    public String getTimeFinish() {
        return delegate.getTimeFinish().toString();
    }
}
