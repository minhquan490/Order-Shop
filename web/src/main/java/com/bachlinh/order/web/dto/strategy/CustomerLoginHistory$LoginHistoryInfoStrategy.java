package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.utils.DateTimeUtils;
import com.bachlinh.order.web.dto.resp.CustomerLoginHistoryResp;

@ActiveReflection
public class CustomerLoginHistory$LoginHistoryInfoStrategy extends AbstractDtoStrategy<CustomerLoginHistoryResp.LoginHistoryInfo, LoginHistory> {

    @ActiveReflection
    private CustomerLoginHistory$LoginHistoryInfoStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
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
    protected DtoStrategy<CustomerLoginHistoryResp.LoginHistoryInfo, LoginHistory> createNew(DependenciesResolver dependenciesResolver, Environment environment) {
        return new CustomerLoginHistory$LoginHistoryInfoStrategy(dependenciesResolver, environment);
    }

    @Override
    public Class<CustomerLoginHistoryResp.LoginHistoryInfo> getTargetType() {
        return CustomerLoginHistoryResp.LoginHistoryInfo.class;
    }
}
