package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.resp.ConfirmEmailResp;

public interface ConfirmEmailService {
    ConfirmEmailResp confirmEmail(String temporaryToken);
}
