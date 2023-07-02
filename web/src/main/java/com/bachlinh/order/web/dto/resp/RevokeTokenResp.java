package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RevokeTokenResp(@JsonProperty("access_token") String accessToken,
                              @JsonProperty("refresh_token") String refreshToken) {
}
