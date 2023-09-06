package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.repository.LoginHistoryRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.web.dto.resp.CustomerLoginHistoryResp;
import com.bachlinh.order.web.service.common.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Collection;

@ServiceComponent
@RequiredArgsConstructor
public class LoginHistoriesServiceImpl implements LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final DtoMapper dtoMapper;
    private final MessageSettingRepository messageSettingRepository;

    @Override
    public CustomerLoginHistoryResp getHistoriesOfCustomer(NativeRequest<?> nativeRequest, String path) {
        long page = getPage(nativeRequest);
        long pageSize = getPageSize(nativeRequest);
        String customerId = getCustomerId(nativeRequest, path);
        Collection<LoginHistory> loginHistories = loginHistoryRepository.getHistoriesOfCustomer(customerId, page, pageSize);
        Long totalHistories = loginHistoryRepository.countHistoriesOfCustomer(customerId);
        var historyInfos = dtoMapper.map(loginHistories, CustomerLoginHistoryResp.LoginHistoryInfo.class);
        CustomerLoginHistoryResp resp = new CustomerLoginHistoryResp();
        resp.setLoginHistories(historyInfos);
        resp.setTotalHistories(totalHistories);
        resp.setPage(page);
        resp.setPageSize(pageSize);
        return resp;
    }

    private String getCustomerId(NativeRequest<?> nativeRequest, String path) {
        String customerId = nativeRequest.getUrlQueryParam().getFirst("customerId");
        if (!StringUtils.hasText(customerId)) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById("MSG-000008");
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Customer");
            throw new ResourceNotFoundException(errorContent, path);
        }
        return customerId;
    }

    private long getPageSize(NativeRequest<?> nativeRequest) {
        String pageSizeRequestParam = nativeRequest.getUrlQueryParam().getFirst("pageSize");
        if (ValidateUtils.isNumber(pageSizeRequestParam)) {
            return Long.parseLong(pageSizeRequestParam);
        } else {
            return 50L;
        }
    }

    private long getPage(NativeRequest<?> nativeRequest) {
        String pageRequestParam = nativeRequest.getUrlQueryParam().getFirst("page");
        if (ValidateUtils.isNumber(pageRequestParam)) {
            return Long.parseLong(pageRequestParam);
        } else {
            return 1L;
        }
    }
}
