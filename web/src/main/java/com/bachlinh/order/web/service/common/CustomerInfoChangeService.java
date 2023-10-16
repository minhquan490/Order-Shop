package com.bachlinh.order.web.service.common;

import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.web.dto.resp.CustomerUpdateDataHistoriesResp;

public interface CustomerInfoChangeService {
    CustomerUpdateDataHistoriesResp getCustomerInfoChangeHistories(NativeRequest<?> request);
}
