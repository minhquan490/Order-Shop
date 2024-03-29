package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.core.annotation.ServiceComponent;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.service.AbstractService;
import com.bachlinh.order.handler.service.ServiceBase;
import com.bachlinh.order.web.repository.spi.LoginHistoryRepository;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;
import com.bachlinh.order.core.utils.ValidateUtils;
import com.bachlinh.order.web.dto.resp.CustomerLoginHistoryResp;
import com.bachlinh.order.web.service.common.LoginHistoryService;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Collection;

@ServiceComponent
public class LoginHistoriesServiceImpl extends AbstractService implements LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final DtoMapper dtoMapper;
    private final MessageSettingRepository messageSettingRepository;

    private LoginHistoriesServiceImpl(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.loginHistoryRepository = resolveRepository(LoginHistoryRepository.class);
        this.dtoMapper = resolver.resolveDependencies(DtoMapper.class);
        this.messageSettingRepository = resolveRepository(MessageSettingRepository.class);
    }

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

    @Override
    public ServiceBase getInstance(DependenciesResolver resolver, Environment environment) {
        return new LoginHistoriesServiceImpl(resolver, environment);
    }

    @Override
    public Class<?>[] getServiceTypes() {
        return new Class[]{LoginHistoryService.class};
    }
}
