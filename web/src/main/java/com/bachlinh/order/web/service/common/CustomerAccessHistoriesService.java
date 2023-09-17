package com.bachlinh.order.web.service.common;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.web.dto.resp.CustomerAccessHistoriesResp;

public interface CustomerAccessHistoriesService {
    CustomerAccessHistoriesResp getAccessHistories(NativeRequest<?> request);
}
