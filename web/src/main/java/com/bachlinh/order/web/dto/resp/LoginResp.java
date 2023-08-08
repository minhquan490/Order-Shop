package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResp(@JsonProperty("refresh_token") String refreshToken,
                        @JsonProperty("access_token") String accessToken,
                        @JsonIgnore boolean isLogged,
                        @JsonIgnore String clientSecret) {
}
