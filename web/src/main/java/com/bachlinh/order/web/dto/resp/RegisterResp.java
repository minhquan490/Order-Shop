package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record RegisterResp(String message, @JsonIgnore boolean isError) {

}
