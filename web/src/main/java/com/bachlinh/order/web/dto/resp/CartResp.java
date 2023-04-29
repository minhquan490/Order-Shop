package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("cart_status")
public record CartResp(int code, String message) {
}
