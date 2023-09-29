package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.entity.model.CustomerInfoChangeHistory;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.utils.DateTimeUtils;
import com.bachlinh.order.web.dto.resp.CustomerUpdateDataHistoriesResp;

@ActiveReflection
public class CustomerUpdateDataHistoriesResp$CustomerUpdateDataHistoryInfoStrategy extends AbstractDtoStrategy<CustomerUpdateDataHistoriesResp.CustomerUpdateDataHistoryInfo, CustomerInfoChangeHistory> {
    private CustomerUpdateDataHistoriesResp$CustomerUpdateDataHistoryInfoStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(CustomerInfoChangeHistory source, Class<CustomerUpdateDataHistoriesResp.CustomerUpdateDataHistoryInfo> type) {
        //Do nothing
    }

    @Override
    protected CustomerUpdateDataHistoriesResp.CustomerUpdateDataHistoryInfo doConvert(CustomerInfoChangeHistory source, Class<CustomerUpdateDataHistoriesResp.CustomerUpdateDataHistoryInfo> type) {
        CustomerUpdateDataHistoriesResp.CustomerUpdateDataHistoryInfo result = new CustomerUpdateDataHistoriesResp.CustomerUpdateDataHistoryInfo();
        result.setId(source.getId());
        result.setTimeUpdate(DateTimeUtils.convertOutputDateTime(source.getTimeUpdate()));
        result.setFieldName(source.getFieldName());
        result.setOldValue(source.getOldValue());
        return result;
    }

    @Override
    protected void afterConvert(CustomerInfoChangeHistory source, Class<CustomerUpdateDataHistoriesResp.CustomerUpdateDataHistoryInfo> type) {
        //Do nothing
    }

    @Override
    protected DtoStrategy<CustomerUpdateDataHistoriesResp.CustomerUpdateDataHistoryInfo, CustomerInfoChangeHistory> createNew(DependenciesResolver dependenciesResolver, Environment environment) {
        return new CustomerUpdateDataHistoriesResp$CustomerUpdateDataHistoryInfoStrategy(dependenciesResolver, environment);
    }

    @Override
    public Class<CustomerUpdateDataHistoriesResp.CustomerUpdateDataHistoryInfo> getTargetType() {
        return CustomerUpdateDataHistoriesResp.CustomerUpdateDataHistoryInfo.class;
    }
}
