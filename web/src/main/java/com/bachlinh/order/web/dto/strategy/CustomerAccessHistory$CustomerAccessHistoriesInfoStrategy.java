package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.CustomerAccessHistoriesResp;

@ActiveReflection
public class CustomerAccessHistory$CustomerAccessHistoriesInfoStrategy extends AbstractDtoStrategy<CustomerAccessHistoriesResp.CustomerAccessHistoriesInfo, CustomerAccessHistory> {
    private CustomerAccessHistory$CustomerAccessHistoriesInfoStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(CustomerAccessHistory source, Class<CustomerAccessHistoriesResp.CustomerAccessHistoriesInfo> type) {
        // Do nothing
    }

    @Override
    protected CustomerAccessHistoriesResp.CustomerAccessHistoriesInfo doConvert(CustomerAccessHistory source, Class<CustomerAccessHistoriesResp.CustomerAccessHistoriesInfo> type) {
        CustomerAccessHistoriesResp.CustomerAccessHistoriesInfo result = new CustomerAccessHistoriesResp.CustomerAccessHistoriesInfo();
        result.setId(String.valueOf(source.getId()));
        result.setPathRequest(source.getPathRequest());
        result.setRequestType(source.getRequestType());
        result.setRequestContent(source.getRequestContent());
        result.setCustomerIp(source.getCustomerIp());
        result.setRemoveTime(String.valueOf(source.getRemoveTime()));
        return result;
    }

    @Override
    protected void afterConvert(CustomerAccessHistory source, Class<CustomerAccessHistoriesResp.CustomerAccessHistoriesInfo> type) {
        // Do nothing
    }

    @Override
    protected DtoStrategy<CustomerAccessHistoriesResp.CustomerAccessHistoriesInfo, CustomerAccessHistory> createNew(DependenciesResolver dependenciesResolver, Environment environment) {
        return new CustomerAccessHistory$CustomerAccessHistoriesInfoStrategy(dependenciesResolver, environment);
    }

    @Override
    public Class<CustomerAccessHistoriesResp.CustomerAccessHistoriesInfo> getTargetType() {
        return CustomerAccessHistoriesResp.CustomerAccessHistoriesInfo.class;
    }
}
