package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.resp.EmailInfoResp;

import java.util.Collection;

public interface EmailSearchingService {
    Collection<EmailInfoResp> searchEmail(String query);
}
