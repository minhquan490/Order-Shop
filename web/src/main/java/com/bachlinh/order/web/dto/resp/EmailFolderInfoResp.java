package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmailFolderInfoResp(@JsonProperty("id") String id,
                                  @JsonProperty("name") String name,
                                  @JsonProperty("clean_policy") Integer cleanPolicy) {
}
