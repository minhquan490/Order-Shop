package com.bachlinh.order.web.service.common;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.web.dto.resp.CustomerLoginHistoryResp;

public interface LoginHistoryService {

    CustomerLoginHistoryResp getHistoriesOfCustomer(NativeRequest<?> nativeRequest, String path);
}
