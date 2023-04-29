package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("category")
public record CategoryResp(String id, String name) {
}
