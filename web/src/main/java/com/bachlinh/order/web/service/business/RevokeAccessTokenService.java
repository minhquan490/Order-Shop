package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.resp.RevokeTokenResp;

public interface RevokeAccessTokenService {
    RevokeTokenResp revokeToken(String refreshToken);
}
