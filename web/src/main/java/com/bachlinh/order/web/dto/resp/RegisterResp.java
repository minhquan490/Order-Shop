package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.Nullable;

public record RegisterResp(String message, @JsonIgnore boolean isError, @JsonIgnore @Nullable Exception exception) {

}
