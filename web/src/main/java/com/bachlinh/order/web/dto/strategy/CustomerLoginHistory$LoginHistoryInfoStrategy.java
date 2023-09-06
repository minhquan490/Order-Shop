package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.DateTimeUtils;
import com.bachlinh.order.web.dto.resp.CustomerLoginHistoryResp;

@ActiveReflection
public class CustomerLoginHistory$LoginHistoryInfoStrategy extends AbstractDtoStrategy<CustomerLoginHistoryResp.LoginHistoryInfo, LoginHistory> {

    @ActiveReflection
    public CustomerLoginHistory$LoginHistoryInfoStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(LoginHistory source, Class<CustomerLoginHistoryResp.LoginHistoryInfo> type) {
        // Do nothing
    }

    @Override
    protected CustomerLoginHistoryResp.LoginHistoryInfo doConvert(LoginHistory source, Class<CustomerLoginHistoryResp.LoginHistoryInfo> type) {
        CustomerLoginHistoryResp.LoginHistoryInfo result = new CustomerLoginHistoryResp.LoginHistoryInfo();
        result.setLoginIp(source.getLoginIp());
        result.setId(String.valueOf(source.getId()));
        result.setSuccess(source.getSuccess().toString());
        String processedDateTime = DateTimeUtils.convertOutputDateTime(source.getLastLoginTime());
        result.setLastLoginTime(processedDateTime);
        return result;
    }

    @Override
    protected void afterConvert(LoginHistory source, Class<CustomerLoginHistoryResp.LoginHistoryInfo> type) {
        // Do nothing
    }

    @Override
    public Class<CustomerLoginHistoryResp.LoginHistoryInfo> getTargetType() {
        return CustomerLoginHistoryResp.LoginHistoryInfo.class;
    }
}
