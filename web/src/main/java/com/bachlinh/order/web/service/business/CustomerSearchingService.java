package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.resp.CustomerResp;

import java.util.Collection;

public interface CustomerSearchingService {
    Collection<CustomerResp> search(String query);
}
